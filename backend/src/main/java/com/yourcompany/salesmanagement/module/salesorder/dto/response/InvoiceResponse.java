package com.yourcompany.salesmanagement.module.salesorder.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record InvoiceResponse(
        SalesOrderDetailResponse order,
        InvoicePartyResponse store,
        InvoicePartyResponse branch,
        InvoicePartyResponse customer,
        InvoicePartyResponse cashier,
        List<InvoicePaymentResponse> payments,
        LocalDateTime generatedAt
) {}

