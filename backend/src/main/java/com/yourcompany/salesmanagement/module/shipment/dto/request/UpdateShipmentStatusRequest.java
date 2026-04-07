package com.yourcompany.salesmanagement.module.shipment.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record UpdateShipmentStatusRequest(
        @NotBlank(message = "Status is required")
        String status,

        LocalDateTime shippedAt,
        LocalDateTime deliveredAt
) {}

