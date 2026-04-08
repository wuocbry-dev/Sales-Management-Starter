package com.yourcompany.salesmanagement.module.voucher.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateVoucherRequest(
        Long promotionId,

        @NotBlank(message = "Code is required")
        String code,

        @NotBlank(message = "Discount type is required")
        String discountType,

        @NotNull(message = "Discount value is required")
        @DecimalMin(value = "0", message = "Discount value must be >= 0")
        BigDecimal discountValue,

        @NotNull(message = "Min order amount is required")
        @DecimalMin(value = "0", message = "Min order amount must be >= 0")
        BigDecimal minOrderAmount,

        BigDecimal maxDiscountAmount,

        @NotNull(message = "Usage limit is required")
        Integer usageLimit,

        @NotNull(message = "Start at is required")
        LocalDateTime startAt,

        @NotNull(message = "End at is required")
        LocalDateTime endAt,

        String status
) {}

