package com.yourcompany.salesmanagement.module.shift.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ShiftResponse(
        Long id,
        Long storeId,
        Long branchId,
        Long cashierUserId,
        String status,
        BigDecimal openingCash,
        BigDecimal closingCash,
        LocalDateTime openedAt,
        LocalDateTime closedAt,
        String note
) {}

