package com.yourcompany.salesmanagement.module.integration.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateIntegrationChannelRequest(
        @NotBlank(message = "channelType is required")
        String channelType,

        @NotBlank(message = "channelName is required")
        String channelName,

        @NotBlank(message = "channelCode is required")
        String channelCode,

        String status,

        /**
         * JSON string (optional). Stored as-is; interpreted later per channel type.
         */
        String configJson
) {}

