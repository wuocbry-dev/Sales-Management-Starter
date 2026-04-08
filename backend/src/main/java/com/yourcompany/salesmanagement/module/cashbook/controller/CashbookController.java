package com.yourcompany.salesmanagement.module.cashbook.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.cashbook.dto.response.CashbookEntryResponse;
import com.yourcompany.salesmanagement.module.cashbook.service.CashbookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/cashbook", "/api/cashbook"})
public class CashbookController {
    private final CashbookService cashbookService;

    public CashbookController(CashbookService cashbookService) {
        this.cashbookService = cashbookService;
    }

    @GetMapping
    public BaseResponse<List<CashbookEntryResponse>> list() {
        return BaseResponse.ok("Cashbook entries fetched successfully", cashbookService.list());
    }
}

