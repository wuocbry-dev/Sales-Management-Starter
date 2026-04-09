package com.yourcompany.salesmanagement.module.salesorder.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ApplyVoucherRequest(
        @NotBlank(message = "Voucher code is required")
        String code
) {}

