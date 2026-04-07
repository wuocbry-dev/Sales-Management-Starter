package com.yourcompany.salesmanagement.module.inventory.repository;

import com.yourcompany.salesmanagement.module.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;

import java.math.BigDecimal;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    @Query("""
            select coalesce(sum(i.quantity - i.reservedQuantity), 0)
            from Inventory i
            where i.storeId = :storeId
              and i.branchId = :branchId
              and i.productId = :productId
            """)
    BigDecimal getAvailableQuantityByProduct(@Param("storeId") Long storeId,
                                            @Param("branchId") Long branchId,
                                            @Param("productId") Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Inventory> findFirstByStoreIdAndBranchIdAndProductIdAndVariantIdIsNull(Long storeId, Long branchId, Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Inventory> findByStoreIdAndBranchIdAndProductIdAndVariantId(Long storeId, Long branchId, Long productId, Long variantId);
}

