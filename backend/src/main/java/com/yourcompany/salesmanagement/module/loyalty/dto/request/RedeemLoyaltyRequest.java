package com.yourcompany.salesmanagement.module.loyalty.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RedeemLoyaltyRequest(
        @NotNull(message = "customerId is required")
        Long customerId,

        @Min(value = 1, message = "points must be >= 1")
        int points,

        Long salesOrderId,

        @Size(max = 255, message = "description must be <= 255 chars")
        String description
) {}

