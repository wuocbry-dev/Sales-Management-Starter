package com.yourcompany.salesmanagement.module.purchaseorder.repository;

import com.yourcompany.salesmanagement.module.purchaseorder.entity.PurchaseReturnItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PurchaseReturnItemRepository extends JpaRepository<PurchaseReturnItem, Long> {
    List<PurchaseReturnItem> findAllByPurchaseReturnId(Long purchaseReturnId);

    @Query("""
            select coalesce(sum(i.quantity), 0)
            from PurchaseReturnItem i
            where i.purchaseOrderItemId = :purchaseOrderItemId
            """)
    BigDecimal sumReturnedQtyByPurchaseOrderItemId(@Param("purchaseOrderItemId") Long purchaseOrderItemId);
}

