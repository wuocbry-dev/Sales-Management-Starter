package com.yourcompany.salesmanagement.module.salesorder.repository;

import com.yourcompany.salesmanagement.module.salesorder.entity.SalesOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SalesOrderItemRepository extends JpaRepository<SalesOrderItem, Long> {
    List<SalesOrderItem> findAllBySalesOrderId(Long salesOrderId);

    Optional<SalesOrderItem> findByIdAndSalesOrderId(Long id, Long salesOrderId);
}

