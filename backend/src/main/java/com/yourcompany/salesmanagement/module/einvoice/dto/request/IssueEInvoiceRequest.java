package com.yourcompany.salesmanagement.module.einvoice.dto.request;

import jakarta.validation.constraints.Size;

public record IssueEInvoiceRequest(
        @Size(max = 200, message = "buyerName must be <= 200 chars")
        String buyerName,

        @Size(max = 50, message = "buyerTaxCode must be <= 50 chars")
        String buyerTaxCode,

        @Size(max = 255, message = "buyerAddress must be <= 255 chars")
        String buyerAddress,

        @Size(max = 150, message = "buyerEmail must be <= 150 chars")
        String buyerEmail
) {}

