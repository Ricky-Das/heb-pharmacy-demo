package com.heb_pharmacy.demo.domain.repo;

import com.heb_pharmacy.demo.domain.Inventory;
import com.heb_pharmacy.demo.domain.InventoryId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, InventoryId> {
    Optional<Inventory> findByIdStoreIdAndIdDrugId(Long storeId, Long drugId);
} 