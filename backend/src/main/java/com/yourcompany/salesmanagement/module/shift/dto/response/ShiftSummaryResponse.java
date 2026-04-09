package com.yourcompany.salesmanagement.module.shift.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ShiftSummaryResponse(
        Long shiftId,
        Long storeId,
        Long branchId,
        LocalDateTime from,
        LocalDateTime to,
        long totalOrders,
        BigDecimal grossSales,
        BigDecimal refunds,
        BigDecimal netSales,
        List<PaymentMethodAmount> paymentsByMethod,
        BigDecimal cashbookIn,
        BigDecimal cashbookOut
) {
    public record PaymentMethodAmount(String method, BigDecimal amount) {}
}

