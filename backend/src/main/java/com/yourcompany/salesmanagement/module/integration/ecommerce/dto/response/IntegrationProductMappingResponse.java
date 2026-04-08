package com.yourcompany.salesmanagement.module.integration.ecommerce.dto.response;

public record IntegrationProductMappingResponse(
        Long id,
        Long channelId,
        Long productId,
        Long variantId,
        String externalProductId,
        String externalVariantId,
        String externalSku,
        String status
) {}

