package com.yourcompany.salesmanagement.module.purchaseorder.dto.response;

import java.math.BigDecimal;

public record PurchaseOrderItemResponse(
        Long id,
        Long productId,
        Long variantId,
        BigDecimal quantity,
        BigDecimal receivedQuantity,
        BigDecimal costPrice,
        BigDecimal lineTotal
) {}
