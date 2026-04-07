package com.yourcompany.salesmanagement.module.purchaseorder.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PurchaseOrderDetailResponse(
        Long id,
        String poNumber,
        String status,
        Long supplierId,
        Long branchId,
        LocalDateTime orderDate,
        LocalDateTime expectedDate,
        BigDecimal subtotal,
        BigDecimal discountAmount,
        BigDecimal taxAmount,
        BigDecimal totalAmount,
        String notes,
        List<PurchaseOrderItemResponse> items
) {}
