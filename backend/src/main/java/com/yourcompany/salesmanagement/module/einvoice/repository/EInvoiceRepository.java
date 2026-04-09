package com.yourcompany.salesmanagement.module.einvoice.repository;

import com.yourcompany.salesmanagement.module.einvoice.entity.EInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;

import java.util.Optional;

public interface EInvoiceRepository extends JpaRepository<EInvoice, Long> {
    Optional<EInvoice> findByIdAndStoreId(Long id, Long storeId);

    Optional<EInvoice> findFirstByStoreIdAndSalesOrderIdOrderByIdDesc(Long storeId, Long salesOrderId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<EInvoice> findForUpdateByIdAndStoreId(Long id, Long storeId);
}

