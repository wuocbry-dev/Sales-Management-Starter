package com.yourcompany.salesmanagement.module.integration.ecommerce.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateIntegrationProductMappingRequest(
        @NotNull(message = "channelId is required")
        Long channelId,

        @NotNull(message = "productId is required")
        Long productId,

        Long variantId,

        String externalProductId,
        String externalVariantId,
        String externalSku,

        String status
) {}

