package com.yourcompany.salesmanagement.module.salesorder.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SalesOrderSummaryResponse(
        Long id,
        String orderNumber,
        String status,
        Long branchId,
        BigDecimal totalAmount,
        LocalDateTime orderedAt
) {}

