package com.heb_pharmacy.demo.service;

import com.heb_pharmacy.demo.api.EligibilityResponse;
import com.heb_pharmacy.demo.config.AppProperties;
import com.heb_pharmacy.demo.domain.*;
import com.heb_pharmacy.demo.domain.repo.InventoryRepository;
import com.heb_pharmacy.demo.domain.repo.PrescriptionRepository;
import com.heb_pharmacy.demo.domain.repo.StoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class EligibilityService {

    private final PrescriptionRepository prescriptionRepo;
    private final StoreRepository storeRepo;
    private final InventoryRepository inventoryRepo;
    private final AppProperties props;
    private final Clock clock;

    public EligibilityService(PrescriptionRepository prescriptionRepo,
                              StoreRepository storeRepo,
                              InventoryRepository inventoryRepo,
                              AppProperties props,
                              Clock clock) {
        this.prescriptionRepo = prescriptionRepo;
        this.storeRepo = storeRepo;
        this.inventoryRepo = inventoryRepo;
        this.props = props;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public EligibilityResponse check(String rxNumber, String storeCode) {
        Prescription rx = prescriptionRepo.findByRxNumber(rxNumber)
                .orElseThrow(() -> new IllegalArgumentException("Unknown rxNumber: " + rxNumber));

        Store store = storeRepo.findByCode(storeCode)
                .orElseThrow(() -> new IllegalArgumentException("Unknown storeCode: " + storeCode));

        LocalDate today = LocalDate.now(clock);
        List<ReasonCode> reasons = new ArrayList<>();
        LocalDate nextFillDate = null;

        // 1) Active & not expired
        if (!rx.isActive() || today.isAfter(rx.getExpiresAt())) {
            reasons.add(ReasonCode.RX_EXPIRED);
        }

        // 2) Controlled substances (Schedule II => no refills)
        Drug drug = rx.getDrug();
        if (drug.isControlled() && "II".equalsIgnoreCase(drug.getSchedule())) {
            reasons.add(ReasonCode.CONTROLLED_NO_REFILL);
        }

        // 3) Refills remaining
        int refillsRemaining = Math.max(0, rx.getRefillsAuthorized() - rx.getRefillsUsed());
        if (refillsRemaining <= 0) {
            reasons.add(ReasonCode.NO_REFILLS);
        }

        // 4) Too soon check (if previously filled)
        if (rx.getLastFillAt() != null) {
            int thresholdDays = (int) Math.ceil(drug.getDaysSupplyDefault() * props.getTooSoonPercent());
            LocalDate earliestNext = rx.getLastFillAt().plusDays(thresholdDays);
            if (today.isBefore(earliestNext)) {
                reasons.add(ReasonCode.TOO_SOON);
                nextFillDate = earliestNext;
            }
        }

        // 5) Inventory check at store
        boolean inventoryAvailable = inventoryRepo
                .findByIdStoreIdAndIdDrugId(store.getId(), drug.getId())
                .map(inv -> (inv.getOnHand() - inv.getReserved()) >= rx.getQuantity())
                .orElse(false);
        if (!inventoryAvailable) {
            reasons.add(ReasonCode.NO_INVENTORY);
        }

        boolean eligible = reasons.isEmpty();
        return EligibilityResponse.builder()
                .eligible(eligible)
                .reasons(reasons)
                .nextFillDate(nextFillDate)
                .refillsRemaining(refillsRemaining)
                .inventoryAvailable(inventoryAvailable)
                .build();
    }
} 