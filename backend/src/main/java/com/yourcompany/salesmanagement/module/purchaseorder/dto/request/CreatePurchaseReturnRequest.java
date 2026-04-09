package com.yourcompany.salesmanagement.module.purchaseorder.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreatePurchaseReturnRequest(
        String reason,

        @Valid
        @NotEmpty(message = "Items are required")
        List<CreatePurchaseReturnItemRequest> items
) {}

