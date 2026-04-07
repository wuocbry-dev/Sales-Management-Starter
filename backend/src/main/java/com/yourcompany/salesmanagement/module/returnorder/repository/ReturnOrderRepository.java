package com.yourcompany.salesmanagement.module.returnorder.repository;

import com.yourcompany.salesmanagement.module.returnorder.entity.ReturnOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReturnOrderRepository extends JpaRepository<ReturnOrder, Long> {
    List<ReturnOrder> findAllByStoreIdAndSalesOrderIdOrderByIdDesc(Long storeId, Long salesOrderId);

    Optional<ReturnOrder> findByIdAndStoreId(Long id, Long storeId);
}

