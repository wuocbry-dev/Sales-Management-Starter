package com.yourcompany.salesmanagement.module.cashbook.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateCashbookEntryRequest(
        Long branchId,

        @NotBlank(message = "entryType is required")
        String entryType, // IN / OUT

        @NotBlank(message = "category is required")
        @Size(max = 100, message = "category must be <= 100 chars")
        String category,

        String referenceType,
        Long referenceId,

        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", message = "amount must be > 0")
        BigDecimal amount,

        @Size(max = 255, message = "description must be <= 255 chars")
        String description,

        LocalDateTime occurredAt
) {}

