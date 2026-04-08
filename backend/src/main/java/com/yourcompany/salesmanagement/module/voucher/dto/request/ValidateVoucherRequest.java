package com.yourcompany.salesmanagement.module.voucher.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ValidateVoucherRequest(
        @NotBlank(message = "Code is required")
        String code,

        @NotNull(message = "Order amount is required")
        @DecimalMin(value = "0", message = "Order amount must be >= 0")
        BigDecimal orderAmount
) {}

