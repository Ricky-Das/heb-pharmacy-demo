package com.heb_pharmacy.demo.domain.repo;

import com.heb_pharmacy.demo.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> { } 