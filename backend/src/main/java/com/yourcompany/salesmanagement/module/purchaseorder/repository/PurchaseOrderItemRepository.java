package com.yourcompany.salesmanagement.module.purchaseorder.repository;

import com.yourcompany.salesmanagement.module.purchaseorder.entity.PurchaseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Long> {
    List<PurchaseOrderItem> findAllByPurchaseOrderId(Long purchaseOrderId);

    Optional<PurchaseOrderItem> findByIdAndPurchaseOrderId(Long id, Long purchaseOrderId);
}
