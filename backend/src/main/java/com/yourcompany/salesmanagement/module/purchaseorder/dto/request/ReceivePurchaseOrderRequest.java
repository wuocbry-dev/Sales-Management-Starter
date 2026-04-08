package com.yourcompany.salesmanagement.module.purchaseorder.dto.request;

import jakarta.validation.Valid;

import java.util.List;

/**
 * If {@code lines} is null or empty, all remaining quantity is received for every line item.
 * Otherwise each line specifies how much to receive for that item (capped by remaining).
 */
public record ReceivePurchaseOrderRequest(
        @Valid List<ReceiveLineRequest> lines
) {}
