package com.yourcompany.salesmanagement.module.cashbook.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.cashbook.dto.request.CreateCashbookEntryRequest;
import com.yourcompany.salesmanagement.module.cashbook.dto.response.CashbookEntryResponse;
import com.yourcompany.salesmanagement.module.cashbook.dto.response.CashbookSummaryResponse;
import com.yourcompany.salesmanagement.module.cashbook.service.CashbookService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping({"/api/v1/cashbook", "/api/cashbook"})
public class CashbookController {
    private final CashbookService cashbookService;

    public CashbookController(CashbookService cashbookService) {
        this.cashbookService = cashbookService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CASHBOOK_READ') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<List<CashbookEntryResponse>> list() {
        return BaseResponse.ok("Cashbook entries fetched successfully", cashbookService.list());
    }

    @PostMapping("/entries")
    @PreAuthorize("hasAuthority('CASHBOOK_WRITE') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<CashbookEntryResponse> create(@Valid @RequestBody CreateCashbookEntryRequest request) {
        return BaseResponse.ok("Cashbook entry created successfully", cashbookService.create(request));
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('CASHBOOK_READ') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<CashbookSummaryResponse> summary(
            @RequestParam Long branchId,
            @RequestParam(required = false) LocalDate date
    ) {
        return BaseResponse.ok("Cashbook summary fetched successfully", cashbookService.getSummary(branchId, date));
    }
}

