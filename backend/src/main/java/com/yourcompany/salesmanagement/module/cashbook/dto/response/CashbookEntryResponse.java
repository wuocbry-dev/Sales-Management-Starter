package com.yourcompany.salesmanagement.module.cashbook.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CashbookEntryResponse(
        Long id,
        Long storeId,
        Long branchId,
        String entryType,
        String category,
        String referenceType,
        Long referenceId,
        BigDecimal amount,
        String description,
        LocalDateTime occurredAt
) {}

