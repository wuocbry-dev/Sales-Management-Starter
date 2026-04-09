package com.yourcompany.salesmanagement.module.purchaseorder.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PurchaseReturnResponse(
        Long id,
        Long purchaseOrderId,
        String returnNumber,
        String status,
        Long supplierId,
        Long branchId,
        BigDecimal totalQuantity,
        BigDecimal totalAmount,
        String reason,
        Long createdBy,
        LocalDateTime createdAt,
        List<PurchaseReturnItemResponse> items
) {}

