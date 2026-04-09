package com.yourcompany.salesmanagement.module.auditlog.dto.response;

import java.time.LocalDateTime;

public record AuditLogResponse(
        Long id,
        Long storeId,
        Long branchId,
        Long actorUserId,
        String actorUsername,
        String module,
        String action,
        String entityType,
        Long entityId,
        String message,
        String ip,
        String userAgent,
        LocalDateTime createdAt
) {}

