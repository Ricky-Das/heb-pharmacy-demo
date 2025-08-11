package com.heb_pharmacy.demo.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "drug")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Drug {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ndc;

    @Column(nullable = false)
    private String name;

    @Column(name = "days_supply_default", nullable = false)
    private int daysSupplyDefault;

    @Column(name = "is_controlled", nullable = false)
    private boolean controlled;

    private String schedule; // e.g., "II"
} 