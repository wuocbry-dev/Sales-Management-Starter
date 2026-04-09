package com.yourcompany.salesmanagement.module.report.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record EndOfDayReportResponse(
        Long storeId,
        Long branchId,
        LocalDate date,

        BigDecimal grossSales,
        BigDecimal refunds,
        BigDecimal netSales,
        Long totalOrders,

        List<EndOfDayPaymentMethodSummary> paymentsByMethod,
        EndOfDayCashbookSummary cashbook
) {}

