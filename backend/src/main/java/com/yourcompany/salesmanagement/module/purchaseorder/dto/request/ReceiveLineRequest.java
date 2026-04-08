package com.yourcompany.salesmanagement.module.purchaseorder.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ReceiveLineRequest(
        @NotNull(message = "Item id is required")
        Long itemId,

        /**
         * Quantity to receive; if null, receive all remaining for this line.
         */
        BigDecimal quantity
) {}
