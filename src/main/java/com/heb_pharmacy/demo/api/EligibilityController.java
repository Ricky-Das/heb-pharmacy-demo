package com.heb_pharmacy.demo.api;

import com.heb_pharmacy.demo.service.EligibilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Refills")
public class EligibilityController {

    private final EligibilityService service;

    public EligibilityController(EligibilityService service) {
        this.service = service;
    }

    @Operation(summary = "Check refill eligibility for a prescription at a given store")
    @GetMapping("/prescriptions/{rxNumber}/eligibility")
    public ResponseEntity<EligibilityResponse> checkEligibility(
            @Parameter(description = "Prescription number") @PathVariable String rxNumber,
            @Parameter(description = "Store code where refill is requested") @RequestParam String storeCode) {

        var resp = service.check(rxNumber, storeCode);
        return ResponseEntity.ok(resp);
    }
} 