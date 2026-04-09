package com.yourcompany.salesmanagement.module.voucher.dto.response;

import java.math.BigDecimal;

public record VoucherValidationResponse(
        boolean valid,
        String message,
        String code,
        String discountType,
        BigDecimal discountValue,
        BigDecimal maxDiscountAmount
) {}

