package com.yourcompany.salesmanagement.module.promotion.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreatePromotionRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Code is required")
        String code,

        @NotBlank(message = "Promotion type is required")
        String promotionType,

        @NotBlank(message = "Value type is required")
        String valueType,

        @NotNull(message = "Value amount is required")
        @DecimalMin(value = "0", message = "Value amount must be >= 0")
        BigDecimal valueAmount,

        @NotNull(message = "Min order amount is required")
        @DecimalMin(value = "0", message = "Min order amount must be >= 0")
        BigDecimal minOrderAmount,

        BigDecimal maxDiscountAmount,

        @NotNull(message = "Start at is required")
        LocalDateTime startAt,

        @NotNull(message = "End at is required")
        LocalDateTime endAt,

        String status
) {}

