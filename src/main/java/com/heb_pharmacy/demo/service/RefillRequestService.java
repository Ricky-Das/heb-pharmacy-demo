package com.heb_pharmacy.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heb_pharmacy.demo.api.EligibilityResponse;
import com.heb_pharmacy.demo.api.RefillResultResponse;
import com.heb_pharmacy.demo.domain.*;
import com.heb_pharmacy.demo.domain.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class RefillRequestService {

    private final PrescriptionRepository prescriptionRepo;
    private final StoreRepository storeRepo;
    private final InventoryRepository inventoryRepo;
    private final RefillRequestRepository refillRepo;
    private final AuditLogRepository auditRepo;
    private final OutboxRepository outboxRepo;
    private final EligibilityService eligibilityService;
    private final ObjectMapper om;
    private final Clock clock;

    public RefillRequestService(PrescriptionRepository prescriptionRepo,
                                StoreRepository storeRepo,
                                InventoryRepository inventoryRepo,
                                RefillRequestRepository refillRepo,
                                AuditLogRepository auditRepo,
                                OutboxRepository outboxRepo,
                                EligibilityService eligibilityService,
                                ObjectMapper om,
                                Clock clock) {
        this.prescriptionRepo = prescriptionRepo;
        this.storeRepo = storeRepo;
        this.inventoryRepo = inventoryRepo;
        this.refillRepo = refillRepo;
        this.auditRepo = auditRepo;
        this.outboxRepo = outboxRepo;
        this.eligibilityService = eligibilityService;
        this.om = om;
        this.clock = clock;
    }

    @Transactional
    public RefillResultResponse requestRefill(String rxNumber, String storeCode) {
        Prescription rx = prescriptionRepo.findByRxNumber(rxNumber)
                .orElseThrow(() -> new IllegalArgumentException("Unknown rxNumber: " + rxNumber));
        Store store = storeRepo.findByCode(storeCode)
                .orElseThrow(() -> new IllegalArgumentException("Unknown storeCode: " + storeCode));

        // Step 1: Evaluate eligibility (uses read-only rules)
        EligibilityResponse er = eligibilityService.check(rxNumber, storeCode);

        // Step 2: If not eligible, record denial + audit + outbox
        if (!er.isEligible()) {
            ReasonCode primary = er.getReasons().isEmpty() ? null : er.getReasons().get(0);
            RefillRequest rr = RefillRequest.builder()
                    .prescription(rx)
                    .store(store)
                    .status(RefillStatus.DENIED)
                    .reasonCode(primary)
                    .build();
            refillRepo.save(rr);

            writeAudit(rr.getId(), "RefillDenied", json("rxNumber", rxNumber, "storeCode", storeCode, "reasons", er.getReasons()));
            writeOutbox(rr.getId(), "RefillDenied", json("rxNumber", rxNumber, "storeCode", storeCode, "reasons", er.getReasons()));

            return RefillResultResponse.builder()
                    .requestId(rr.getId())
                    .status(RefillStatus.DENIED)
                    .event("RefillDenied")
                    .reason(primary)
                    .build();
        }

        // Step 3: Eligible path — lock inventory row and re-check under lock
        Inventory inv = inventoryRepo.findForUpdate(store.getId(), rx.getDrug().getId())
                .orElseThrow(() -> new IllegalStateException("Inventory row missing for store=" + storeCode + " drugId=" + rx.getDrug().getId()));

        int available = inv.getOnHand() - inv.getReserved();
        if (available < rx.getQuantity()) {
            // inventory changed since eligibility check → deny as NO_INVENTORY
            RefillRequest rr = RefillRequest.builder()
                    .prescription(rx).store(store)
                    .status(RefillStatus.DENIED)
                    .reasonCode(ReasonCode.NO_INVENTORY).build();
            refillRepo.save(rr);

            writeAudit(rr.getId(), "RefillDenied", json("rxNumber", rxNumber, "storeCode", storeCode, "reasons", List.of(ReasonCode.NO_INVENTORY)));
            writeOutbox(rr.getId(), "RefillDenied", json("rxNumber", rxNumber, "storeCode", storeCode, "reasons", List.of(ReasonCode.NO_INVENTORY)));

            return RefillResultResponse.builder()
                    .requestId(rr.getId())
                    .status(RefillStatus.DENIED)
                    .event("RefillDenied")
                    .reason(ReasonCode.NO_INVENTORY)
                    .build();
        }

        // Step 4: Reserve inventory atomically
        inv.setReserved(inv.getReserved() + rx.getQuantity());
        inventoryRepo.save(inv);

        RefillRequest rr = RefillRequest.builder()
                .prescription(rx)
                .store(store)
                .status(RefillStatus.RESERVED)
                .reasonCode(null)
                .build();
        refillRepo.save(rr);

        writeAudit(rr.getId(), "InventoryReserved", json("rxNumber", rxNumber, "storeCode", storeCode, "quantity", rx.getQuantity()));
        writeOutbox(rr.getId(), "InventoryReserved", json("rxNumber", rxNumber, "storeCode", storeCode, "quantity", rx.getQuantity()));

        return RefillResultResponse.builder()
                .requestId(rr.getId())
                .status(RefillStatus.RESERVED)
                .event("InventoryReserved")
                .reason(null)
                .build();
    }

    private void writeAudit(Long entityId, String event, String detailsJson) {
        auditRepo.save(AuditLog.builder()
                .entity("RefillRequest")
                .entityId(entityId)
                .event(event)
                .detailsJson(detailsJson)
                .build());
    }

    private void writeOutbox(Long aggregateId, String eventType, String payloadJson) {
        outboxRepo.save(Outbox.builder()
                .aggregateType("RefillRequest")
                .aggregateId(aggregateId)
                .eventType(eventType)
                .payloadJson(payloadJson)
                .publishedAt(null) // pending; could be published by a scheduler
                .build());
    }

    private String json(Object... kv) {
        try {
            var node = om.createObjectNode();
            for (int i = 0; i < kv.length; i += 2) {
                String key = String.valueOf(kv[i]);
                Object val = kv[i + 1];
                node.set(key, om.valueToTree(val));
            }
            return om.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize JSON payload", e);
        }
    }
} 