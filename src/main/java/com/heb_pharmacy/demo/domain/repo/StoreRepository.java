package com.heb_pharmacy.demo.domain.repo;

import com.heb_pharmacy.demo.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByCode(String code);
} 