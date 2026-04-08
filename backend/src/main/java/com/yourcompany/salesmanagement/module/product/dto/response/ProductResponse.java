package com.yourcompany.salesmanagement.module.product.dto.response;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        Long categoryId,
        String categoryName,
        Double sellingPrice,
        Boolean trackInventory,
        String status
) {}
