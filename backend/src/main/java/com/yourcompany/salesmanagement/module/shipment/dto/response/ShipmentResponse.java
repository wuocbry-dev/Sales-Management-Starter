package com.yourcompany.salesmanagement.module.shipment.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ShipmentResponse(
        Long id,
        Long salesOrderId,
        String shipmentCode,
        String carrierName,
        String serviceName,
        String trackingNumber,
        String status,
        String receiverName,
        String receiverPhone,
        String receiverAddress,
        BigDecimal shippingFee,
        BigDecimal codAmount,
        LocalDateTime shippedAt,
        LocalDateTime deliveredAt
) {}

