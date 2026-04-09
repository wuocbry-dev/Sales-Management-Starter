package com.yourcompany.salesmanagement.module.einvoice.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.einvoice.dto.request.IssueEInvoiceRequest;
import com.yourcompany.salesmanagement.module.einvoice.dto.response.EInvoiceResponse;
import com.yourcompany.salesmanagement.module.einvoice.service.EInvoiceService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/v1/e-invoices", "/api/e-invoices"})
public class EInvoiceController {
    private final EInvoiceService eInvoiceService;

    public EInvoiceController(EInvoiceService eInvoiceService) {
        this.eInvoiceService = eInvoiceService;
    }

    @PostMapping("/issue")
    @PreAuthorize("hasAuthority('EINVOICE_ISSUE') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<EInvoiceResponse> issue(
            @RequestParam Long salesOrderId,
            @Valid @RequestBody(required = false) IssueEInvoiceRequest request
    ) {
        return BaseResponse.ok("E-invoice issue started", eInvoiceService.issue(salesOrderId, request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('EINVOICE_READ') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<EInvoiceResponse> get(@PathVariable Long id) {
        return BaseResponse.ok("E-invoice fetched successfully", eInvoiceService.getById(id));
    }
}

