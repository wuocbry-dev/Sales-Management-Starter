package com.yourcompany.salesmanagement.module.auditlog.repository;

import com.yourcompany.salesmanagement.module.auditlog.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}

