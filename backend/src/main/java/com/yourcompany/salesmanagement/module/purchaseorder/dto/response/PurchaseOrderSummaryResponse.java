package com.yourcompany.salesmanagement.module.purchaseorder.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PurchaseOrderSummaryResponse(
        Long id,
        String poNumber,
        String status,
        Long supplierId,
        Long branchId,
        LocalDateTime orderDate,
        BigDecimal totalAmount
) {}
