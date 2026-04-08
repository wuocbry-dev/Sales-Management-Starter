package com.yourcompany.salesmanagement.module.variant.dto.response;

public record VariantResponse(
        Long id,
        Long productId,
        String sku,
        String barcode,
        String variantName,
        double costPrice,
        double sellingPrice,
        String status
) {}

