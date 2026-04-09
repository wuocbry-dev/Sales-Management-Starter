package com.yourcompany.salesmanagement.module.cashbook.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CashbookSummaryResponse(
        Long storeId,
        Long branchId,
        LocalDate date,
        BigDecimal totalIn,
        BigDecimal totalOut,
        BigDecimal net
) {}

