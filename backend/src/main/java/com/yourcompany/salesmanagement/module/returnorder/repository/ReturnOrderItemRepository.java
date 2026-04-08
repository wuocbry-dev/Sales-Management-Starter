package com.yourcompany.salesmanagement.module.returnorder.repository;

import com.yourcompany.salesmanagement.module.returnorder.entity.ReturnOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ReturnOrderItemRepository extends JpaRepository<ReturnOrderItem, Long> {
    List<ReturnOrderItem> findAllByReturnOrderId(Long returnOrderId);

    @Query("""
            select coalesce(sum(ri.quantity), 0)
            from ReturnOrderItem ri
            where ri.salesOrderItemId = :salesOrderItemId
            """)
    BigDecimal sumReturnedQtyBySalesOrderItemId(@Param("salesOrderItemId") Long salesOrderItemId);
}

