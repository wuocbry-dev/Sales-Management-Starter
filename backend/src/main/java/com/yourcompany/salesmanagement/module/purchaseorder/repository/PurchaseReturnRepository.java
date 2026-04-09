package com.yourcompany.salesmanagement.module.purchaseorder.repository;

import com.yourcompany.salesmanagement.module.purchaseorder.entity.PurchaseReturn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseReturnRepository extends JpaRepository<PurchaseReturn, Long> {
    List<PurchaseReturn> findAllByStoreIdAndPurchaseOrderIdOrderByIdDesc(Long storeId, Long purchaseOrderId);
}

