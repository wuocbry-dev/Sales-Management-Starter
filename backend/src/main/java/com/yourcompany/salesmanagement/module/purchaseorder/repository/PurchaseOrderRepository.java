package com.yourcompany.salesmanagement.module.purchaseorder.repository;

import com.yourcompany.salesmanagement.module.purchaseorder.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findAllByStoreIdOrderByIdDesc(Long storeId);

    Optional<PurchaseOrder> findByIdAndStoreId(Long id, Long storeId);
}
