package com.heb_pharmacy.demo.domain.repo;

import com.heb_pharmacy.demo.domain.Inventory;
import com.heb_pharmacy.demo.domain.InventoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, InventoryId> {
    Optional<Inventory> findByIdStoreIdAndIdDrugId(Long storeId, Long drugId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Inventory i where i.id.storeId = :storeId and i.id.drugId = :drugId")
    Optional<Inventory> findForUpdate(@Param("storeId") Long storeId, @Param("drugId") Long drugId);
} 