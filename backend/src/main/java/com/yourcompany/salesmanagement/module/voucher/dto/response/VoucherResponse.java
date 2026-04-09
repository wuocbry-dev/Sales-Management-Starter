package com.yourcompany.salesmanagement.module.voucher.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VoucherResponse(
        Long id,
        Long promotionId,
        String code,
        String discountType,
        BigDecimal discountValue,
        BigDecimal minOrderAmount,
        BigDecimal maxDiscountAmount,
        Integer usageLimit,
        Integer usedCount,
        LocalDateTime startAt,
        LocalDateTime endAt,
        String status
) {}

