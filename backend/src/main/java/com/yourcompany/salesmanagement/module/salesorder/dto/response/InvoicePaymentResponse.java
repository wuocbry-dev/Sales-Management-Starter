package com.yourcompany.salesmanagement.module.salesorder.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InvoicePaymentResponse(
        Long id,
        String paymentCode,
        String paymentMethod,
        BigDecimal amount,
        LocalDateTime paidAt,
        String transactionRef,
        String notes
) {}

