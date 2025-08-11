package com.heb_pharmacy.demo.domain.repo;

import com.heb_pharmacy.demo.domain.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxRepository extends JpaRepository<Outbox, Long> { } 