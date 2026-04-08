package com.yourcompany.salesmanagement.module.inventory.repository;

import com.yourcompany.salesmanagement.module.inventory.dto.response.InventoryOverviewResponse;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@NoRepositoryBean
public interface InventoryOverviewRepository extends Repository<Object, Long> {
    @Query(value = """
            select
              store_id as storeId,
              branch_id as branchId,
              product_id as productId,
              sku as sku,
              product_name as productName,
              variant_name as variantName,
              quantity as quantity,
              reserved_quantity as reservedQuantity,
              available_quantity as availableQuantity,
              min_quantity as minQuantity,
              max_quantity as maxQuantity
            from vw_inventory_overview
            where store_id = :storeId and branch_id = :branchId
            order by product_name asc, variant_name asc
            """, nativeQuery = true)
    List<InventoryOverviewResponse> findByStoreIdAndBranchId(@Param("storeId") Long storeId, @Param("branchId") Long branchId);

    @Query(value = """
            select
              store_id as storeId,
              branch_id as branchId,
              product_id as productId,
              sku as sku,
              product_name as productName,
              variant_name as variantName,
              quantity as quantity,
              reserved_quantity as reservedQuantity,
              available_quantity as availableQuantity,
              min_quantity as minQuantity,
              max_quantity as maxQuantity
            from vw_inventory_overview
            where store_id = :storeId
              and branch_id = :branchId
              and available_quantity <= min_quantity
            order by available_quantity asc, product_name asc, variant_name asc
            """, nativeQuery = true)
    List<InventoryOverviewResponse> findWarningsByStoreIdAndBranchId(@Param("storeId") Long storeId, @Param("branchId") Long branchId);
}

