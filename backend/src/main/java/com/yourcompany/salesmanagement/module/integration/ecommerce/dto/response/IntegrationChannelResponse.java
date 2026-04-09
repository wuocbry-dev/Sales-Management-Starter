package com.yourcompany.salesmanagement.module.integration.ecommerce.dto.response;

public record IntegrationChannelResponse(
        Long id,
        String channelType,
        String channelName,
        String channelCode,
        String status,
        String configJson
) {}

