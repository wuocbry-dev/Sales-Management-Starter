package com.yourcompany.salesmanagement.module.loyalty.dto.response;

import java.time.LocalDateTime;

public record LoyaltyTransactionResponse(
        Long id,
        String referenceType,
        Long referenceId,
        int pointsChange,
        String description,
        LocalDateTime createdAt
) {}

