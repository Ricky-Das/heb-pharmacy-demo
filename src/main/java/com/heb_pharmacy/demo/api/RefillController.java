package com.heb_pharmacy.demo.api;

import com.heb_pharmacy.demo.domain.RefillRequest;
import com.heb_pharmacy.demo.domain.repo.RefillRequestRepository;
import com.heb_pharmacy.demo.service.RefillRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Refills")
public class RefillController {

    private final RefillRequestService service;
    private final RefillRequestRepository refillRepo;

    public RefillController(RefillRequestService service,
                            RefillRequestRepository refillRepo) {
        this.service = service;
        this.refillRepo = refillRepo;
    }

    @Operation(summary = "Create a refill request; reserves inventory if eligible")
    @PostMapping("/prescriptions/{rxNumber}/refill-requests")
    public ResponseEntity<RefillResultResponse> createRefill(
            @PathVariable String rxNumber,
            @Valid @RequestBody CreateRefillRequest body) {
        var resp = service.requestRefill(rxNumber, body.getStoreCode());
        // 201 for RESERVED; 409 for DENIED
        if (resp.getStatus().name().equals("DENIED")) {
            return ResponseEntity.status(409).body(resp);
        }
        return ResponseEntity.status(201).body(resp);
    }

    @Operation(summary = "Lookup a refill request (basic)")
    @GetMapping("/refill-requests/{id}")
    public ResponseEntity<RefillRequest> getRefill(@PathVariable Long id) {
        return refillRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 