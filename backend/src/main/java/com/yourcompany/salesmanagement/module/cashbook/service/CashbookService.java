package com.yourcompany.salesmanagement.module.cashbook.service;

import com.yourcompany.salesmanagement.module.cashbook.dto.response.CashbookEntryResponse;

import java.util.List;

public interface CashbookService {
    List<CashbookEntryResponse> list();
}

