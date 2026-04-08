package com.yourcompany.salesmanagement.module.purchaseorder.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreatePurchaseOrderRequest(
        @NotNull(message = "Supplier is required")
        Long supplierId,

        @NotNull(message = "Branch is required")
        Long branchId,

        LocalDateTime orderDate,

        LocalDateTime expectedDate,

        String notes
) {}
