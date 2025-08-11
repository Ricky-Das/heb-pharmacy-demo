package com.heb_pharmacy.demo.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "refill_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefillRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefillStatus status;                 // RESERVED or DENIED

    @Enumerated(EnumType.STRING)
    @Column(name = "reason_code")
    private ReasonCode reasonCode;               // nullable for RESERVED
} 