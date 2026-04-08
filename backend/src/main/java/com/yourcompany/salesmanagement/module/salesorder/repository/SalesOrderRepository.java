package com.yourcompany.salesmanagement.module.salesorder.repository;

import com.yourcompany.salesmanagement.module.salesorder.entity.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {
    List<SalesOrder> findAllByStoreIdOrderByIdDesc(Long storeId);

    Optional<SalesOrder> findByIdAndStoreId(Long id, Long storeId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<SalesOrder> findForUpdateByIdAndStoreId(Long id, Long storeId);
}

