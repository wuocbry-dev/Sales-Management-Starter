package com.yourcompany.salesmanagement.module.purchaseorder.repository;

import com.yourcompany.salesmanagement.module.purchaseorder.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findAllByStoreIdOrderByIdDesc(Long storeId);

    Optional<PurchaseOrder> findByIdAndStoreId(Long id, Long storeId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PurchaseOrder> findForUpdateByIdAndStoreId(Long id, Long storeId);
}
