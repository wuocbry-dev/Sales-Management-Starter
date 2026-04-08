package com.yourcompany.salesmanagement.module.payment.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreatePaymentRequest(
        @NotNull(message = "Sales order id is required")
        Long salesOrderId,

        @NotBlank(message = "Payment method is required")
        String paymentMethod,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be > 0")
        BigDecimal amount,

        LocalDateTime paidAt,

        String transactionRef,

        String notes
) {}

