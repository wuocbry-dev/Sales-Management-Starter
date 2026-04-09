package com.yourcompany.salesmanagement.module.cashbook.service;

import com.yourcompany.salesmanagement.module.cashbook.dto.request.CreateCashbookEntryRequest;
import com.yourcompany.salesmanagement.module.cashbook.dto.response.CashbookEntryResponse;
import com.yourcompany.salesmanagement.module.cashbook.dto.response.CashbookSummaryResponse;

import java.time.LocalDate;
import java.util.List;

public interface CashbookService {
    List<CashbookEntryResponse> list();

    CashbookEntryResponse create(CreateCashbookEntryRequest request);

    CashbookSummaryResponse getSummary(Long branchId, LocalDate date);
}

