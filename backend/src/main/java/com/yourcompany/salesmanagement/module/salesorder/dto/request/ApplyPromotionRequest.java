package com.yourcompany.salesmanagement.module.salesorder.dto.request;

import jakarta.validation.constraints.NotNull;

public record ApplyPromotionRequest(
        @NotNull(message = "promotionId is required")
        Long promotionId
) {}

