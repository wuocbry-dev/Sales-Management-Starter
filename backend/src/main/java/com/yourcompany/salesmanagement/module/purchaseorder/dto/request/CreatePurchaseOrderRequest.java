package com.yourcompany.salesmanagement.module.purchaseorder.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record CreatePurchaseOrderRequest(
        @NotNull(message = "Supplier is required")
        Long supplierId,

        @NotNull(message = "Branch is required")
        Long branchId,

        LocalDateTime orderDate,

        LocalDateTime expectedDate,

        String notes,

        /**
         * Optional. If provided and not empty, items will be created in the same request.
         */
        List<AddPurchaseOrderItemRequest> items,

        /**
         * Optional. If true (default when {@code items} present), the PO will be received immediately,
         * increasing inventory quantities for its branch.
         */
        Boolean receiveNow
) {}
