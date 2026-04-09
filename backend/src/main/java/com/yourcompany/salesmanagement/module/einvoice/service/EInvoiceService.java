package com.yourcompany.salesmanagement.module.einvoice.service;

import com.yourcompany.salesmanagement.module.einvoice.dto.request.IssueEInvoiceRequest;
import com.yourcompany.salesmanagement.module.einvoice.dto.response.EInvoiceResponse;

public interface EInvoiceService {
    EInvoiceResponse issue(Long salesOrderId, IssueEInvoiceRequest request);

    EInvoiceResponse getById(Long id);
}

