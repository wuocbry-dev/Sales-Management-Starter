package com.yourcompany.salesmanagement.module.salesorder.dto.response;

import java.math.BigDecimal;

public record SalesOrderItemResponse(
        Long id,
        Long productId,
        Long variantId,
        String productName,
        String sku,
        BigDecimal unitPrice,
        BigDecimal quantity,
        BigDecimal discountAmount,
        BigDecimal lineTotal
) {}

