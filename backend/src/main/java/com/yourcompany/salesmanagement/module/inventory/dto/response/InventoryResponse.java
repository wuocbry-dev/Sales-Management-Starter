package com.yourcompany.salesmanagement.module.inventory.dto.response;

import java.math.BigDecimal;

public record InventoryResponse(
        Long id,
        Long storeId,
        Long branchId,
        Long productId,
        Long variantId,
        BigDecimal quantity,
        BigDecimal reservedQuantity,
        BigDecimal minQuantity,
        BigDecimal maxQuantity
) {}

