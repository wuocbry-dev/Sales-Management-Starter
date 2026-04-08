package com.yourcompany.salesmanagement.module.dashboard.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SalesDailySummaryResponse(
        Long storeId,
        Long branchId,
        LocalDate saleDate,
        long totalOrders,
        BigDecimal grossRevenue,
        BigDecimal collectedAmount
) {}

