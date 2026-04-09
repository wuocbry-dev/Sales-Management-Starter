package com.yourcompany.salesmanagement.module.salesorder.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SalesOrderDetailResponse(
        Long id,
        String orderNumber,
        String status,
        Long storeId,
        Long branchId,
        Long customerId,
        BigDecimal subtotal,
        BigDecimal discountAmount,
        BigDecimal taxAmount,
        BigDecimal shippingFee,
        BigDecimal totalAmount,
        BigDecimal paidAmount,
        String discountSource,
        String appliedVoucherCode,
        Long appliedPromotionId,
        String appliedPromotionCode,
        String notes,
        Long soldBy,
        LocalDateTime orderedAt,
        List<SalesOrderItemResponse> items
) {}

