package com.heb_pharmacy.demo.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "prescription")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rx_number", nullable = false, unique = true)
    private String rxNumber;

    @ManyToOne(fetch = FetchType.EAGER) // need drug fields for rules
    @JoinColumn(name = "drug_id", nullable = false)
    private Drug drug;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "refills_authorized", nullable = false)
    private int refillsAuthorized;

    @Column(name = "refills_used", nullable = false)
    private int refillsUsed;

    @Column(name = "written_date", nullable = false)
    private LocalDate writtenDate;

    @Column(name = "expires_at", nullable = false)
    private LocalDate expiresAt;

    @Column(name = "last_fill_at")
    private LocalDate lastFillAt;

    @Column(name = "is_active", nullable = false)
    private boolean active;
} 