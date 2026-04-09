package com.yourcompany.salesmanagement.module.integration.ecommerce.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OnlineOrderResponse(
        Long id,
        Long channelId,
        String externalOrderId,
        String externalOrderNumber,
        String status,
        String buyerName,
        String buyerPhone,
        BigDecimal totalAmount,
        LocalDateTime syncedAt,
        LocalDateTime createdAt
) {}

