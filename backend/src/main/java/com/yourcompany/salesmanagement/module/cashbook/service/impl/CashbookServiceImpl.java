package com.yourcompany.salesmanagement.module.cashbook.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.module.cashbook.dto.response.CashbookEntryResponse;
import com.yourcompany.salesmanagement.module.cashbook.repository.CashbookEntryRepository;
import com.yourcompany.salesmanagement.module.cashbook.service.CashbookService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CashbookServiceImpl implements CashbookService {
    private final CashbookEntryRepository cashbookEntryRepository;

    public CashbookServiceImpl(CashbookEntryRepository cashbookEntryRepository) {
        this.cashbookEntryRepository = cashbookEntryRepository;
    }

    @Override
    public List<CashbookEntryResponse> list() {
        Long storeId = SecurityUtils.requireStoreId();
        return cashbookEntryRepository.findAllByStoreIdOrderByOccurredAtDescIdDesc(storeId).stream()
                .map(e -> new CashbookEntryResponse(
                        e.getId(),
                        e.getStoreId(),
                        e.getBranchId(),
                        e.getEntryType(),
                        e.getCategory(),
                        e.getReferenceType(),
                        e.getReferenceId(),
                        e.getAmount(),
                        e.getDescription(),
                        e.getOccurredAt()
                ))
                .toList();
    }
}

