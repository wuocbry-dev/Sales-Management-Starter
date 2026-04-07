package com.yourcompany.salesmanagement.module.payment.dto.response;

import java.math.BigDecimal;

public record OrderPaymentStatusResponse(
        Long salesOrderId,
        BigDecimal totalAmount,
        BigDecimal paidAmount,
        BigDecimal remainingAmount,
        String paymentStatus
) {}

