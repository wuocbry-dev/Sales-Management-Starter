package com.yourcompany.salesmanagement.module.auditlog.service;

import com.yourcompany.salesmanagement.module.auditlog.dto.response.AuditLogResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogService {
    void write(Long storeId,
               Long branchId,
               Long actorUserId,
               String actorUsername,
               String module,
               String action,
               String entityType,
               Long entityId,
               String message,
               String ip,
               String userAgent);

    List<AuditLogResponse> search(LocalDateTime from,
                                 LocalDateTime to,
                                 Long actorUserId,
                                 String module,
                                 String action,
                                 Long branchId,
                                 Integer limit,
                                 Integer offset);
}

