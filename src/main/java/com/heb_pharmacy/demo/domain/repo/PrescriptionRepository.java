package com.heb_pharmacy.demo.domain.repo;

import com.heb_pharmacy.demo.domain.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    Optional<Prescription> findByRxNumber(String rxNumber);
} 