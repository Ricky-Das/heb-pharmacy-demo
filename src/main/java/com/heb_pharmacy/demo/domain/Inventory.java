package com.heb_pharmacy.demo.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @EmbeddedId
    private InventoryId id;

    @Column(name = "on_hand", nullable = false)
    private int onHand;

    @Column(name = "reserved", nullable = false)
    private int reserved;
} 