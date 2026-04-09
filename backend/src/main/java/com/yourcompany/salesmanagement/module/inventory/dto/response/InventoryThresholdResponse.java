package com.yourcompany.salesmanagement.module.inventory.dto.response;

import java.math.BigDecimal;

public record InventoryThresholdResponse(
        Long id,
        Long storeId,
        Long branchId,
        Long productId,
        Long variantId,
        BigDecimal minQuantity,
        BigDecimal maxQuantity
) {}

