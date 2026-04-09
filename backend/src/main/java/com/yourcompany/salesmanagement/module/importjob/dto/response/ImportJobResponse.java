package com.yourcompany.salesmanagement.module.importjob.dto.response;

import java.time.LocalDateTime;

public record ImportJobResponse(
        Long id,
        Long storeId,
        String type,
        String status,
        String originalFilename,
        Integer totalRows,
        Integer successRows,
        Integer failedRows,
        String errorMessage,
        LocalDateTime createdAt,
        LocalDateTime startedAt,
        LocalDateTime finishedAt
) {}

