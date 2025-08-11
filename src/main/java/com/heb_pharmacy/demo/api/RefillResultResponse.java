package com.heb_pharmacy.demo.api;

import com.heb_pharmacy.demo.domain.ReasonCode;
import com.heb_pharmacy.demo.domain.RefillStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefillResultResponse {
    private Long requestId;
    private RefillStatus status;        // RESERVED or DENIED
    private String event;               // "InventoryReserved" or "RefillDenied"
    private ReasonCode reason;          // null when RESERVED
} 