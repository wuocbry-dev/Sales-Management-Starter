package com.yourcompany.salesmanagement.module.cashbook.repository;

import com.yourcompany.salesmanagement.module.cashbook.entity.CashbookEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CashbookEntryRepository extends JpaRepository<CashbookEntry, Long> {
    List<CashbookEntry> findAllByStoreIdOrderByOccurredAtDescIdDesc(Long storeId);
}

