package com.yourcompany.salesmanagement.module.purchaseorder.dto.response;

import java.math.BigDecimal;

public record PurchaseReturnItemResponse(
        Long id,
        Long purchaseOrderItemId,
        Long productId,
        Long variantId,
        BigDecimal quantity,
        BigDecimal unitCost,
        BigDecimal lineTotal
) {}

