package com.yourcompany.salesmanagement.module.payment.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Long salesOrderId,
        String paymentCode,
        String paymentMethod,
        String status,
        BigDecimal amount,
        LocalDateTime paidAt,
        String transactionRef,
        String notes
) {}

