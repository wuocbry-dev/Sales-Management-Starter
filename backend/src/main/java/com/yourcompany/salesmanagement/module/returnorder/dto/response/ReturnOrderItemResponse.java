package com.yourcompany.salesmanagement.module.returnorder.dto.response;

import java.math.BigDecimal;

public record ReturnOrderItemResponse(
        Long id,
        Long salesOrderItemId,
        Long productId,
        Long variantId,
        String productName,
        String sku,
        BigDecimal unitPrice,
        BigDecimal quantity,
        BigDecimal lineTotal
) {}

