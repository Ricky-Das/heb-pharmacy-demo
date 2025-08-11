package com.heb_pharmacy.demo.api;

import com.heb_pharmacy.demo.domain.ReasonCode;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EligibilityResponse {
    private boolean eligible;
    private List<ReasonCode> reasons;
    private LocalDate nextFillDate;      // may be null
    private int refillsRemaining;
    private boolean inventoryAvailable;
} 