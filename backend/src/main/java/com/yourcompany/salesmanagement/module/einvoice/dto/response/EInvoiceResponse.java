package com.yourcompany.salesmanagement.module.einvoice.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EInvoiceResponse(
        Long id,
        Long storeId,
        Long salesOrderId,
        String status,
        String providerName,
        String providerInvoiceId,
        String invoiceNumber,
        String buyerName,
        String buyerTaxCode,
        String buyerAddress,
        String buyerEmail,
        BigDecimal subtotal,
        BigDecimal taxAmount,
        BigDecimal totalAmount,
        LocalDateTime issuedAt,
        String errorMessage,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

