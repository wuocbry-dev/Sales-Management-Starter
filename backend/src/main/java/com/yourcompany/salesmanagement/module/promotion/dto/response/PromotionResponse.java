package com.yourcompany.salesmanagement.module.promotion.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PromotionResponse(
        Long id,
        String name,
        String code,
        String promotionType,
        String valueType,
        BigDecimal valueAmount,
        BigDecimal minOrderAmount,
        BigDecimal maxDiscountAmount,
        LocalDateTime startAt,
        LocalDateTime endAt,
        String status
) {}

