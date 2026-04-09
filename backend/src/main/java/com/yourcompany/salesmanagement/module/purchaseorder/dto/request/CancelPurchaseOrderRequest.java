package com.yourcompany.salesmanagement.module.purchaseorder.dto.request;

import jakarta.validation.constraints.Size;

public record CancelPurchaseOrderRequest(
        @Size(max = 500, message = "reason must be <= 500 chars")
        String reason
) {}

