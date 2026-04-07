package com.yourcompany.salesmanagement.module.returnorder.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ReturnOrderResponse(
        Long id,
        String returnNumber,
        String status,
        Long salesOrderId,
        Long branchId,
        BigDecimal subtotal,
        BigDecimal refundAmount,
        String notes,
        Long createdBy,
        LocalDateTime createdAt,
        List<ReturnOrderItemResponse> items
) {}

