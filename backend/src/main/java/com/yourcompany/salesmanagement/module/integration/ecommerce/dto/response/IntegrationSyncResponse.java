package com.yourcompany.salesmanagement.module.integration.ecommerce.dto.response;

public record IntegrationSyncResponse(
        Long channelId,
        String status, // STARTED
        String message
) {}

