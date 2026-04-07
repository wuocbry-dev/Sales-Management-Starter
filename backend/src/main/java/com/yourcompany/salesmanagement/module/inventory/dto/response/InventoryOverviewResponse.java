package com.yourcompany.salesmanagement.module.inventory.dto.response;

import java.math.BigDecimal;

public record InventoryOverviewResponse(
        Long storeId,
        Long branchId,
        Long productId,
        String sku,
        String productName,
        String variantName,
        BigDecimal quantity,
        BigDecimal reservedQuantity,
        BigDecimal availableQuantity,
        BigDecimal minQuantity,
        BigDecimal maxQuantity
) {}

