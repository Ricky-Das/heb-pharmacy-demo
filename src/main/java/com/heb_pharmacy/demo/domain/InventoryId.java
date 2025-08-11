package com.heb_pharmacy.demo.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class InventoryId implements Serializable {
    private Long storeId;
    private Long drugId;
} 