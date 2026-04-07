package com.yourcompany.salesmanagement.module.shipment.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateShipmentRequest(
        @NotNull(message = "Sales order id is required")
        Long salesOrderId,

        String carrierName,
        String serviceName,
        String trackingNumber,

        @NotBlank(message = "Receiver name is required")
        String receiverName,

        @NotBlank(message = "Receiver phone is required")
        String receiverPhone,

        @NotBlank(message = "Receiver address is required")
        String receiverAddress,

        @NotNull(message = "Shipping fee is required")
        @DecimalMin(value = "0", message = "Shipping fee must be >= 0")
        BigDecimal shippingFee,

        @NotNull(message = "COD amount is required")
        @DecimalMin(value = "0", message = "COD amount must be >= 0")
        BigDecimal codAmount
) {}

