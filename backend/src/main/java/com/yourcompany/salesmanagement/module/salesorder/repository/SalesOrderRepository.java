package com.yourcompany.salesmanagement.module.salesorder.repository;

import com.yourcompany.salesmanagement.module.salesorder.entity.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {
    List<SalesOrder> findAllByStoreIdOrderByIdDesc(Long storeId);

    Optional<SalesOrder> findByIdAndStoreId(Long id, Long storeId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<SalesOrder> findForUpdateByIdAndStoreId(Long id, Long storeId);

    @Query("""
            select so
            from SalesOrder so
            where so.storeId = :storeId
              and so.customerId = :customerId
              and (:branchId is null or so.branchId = :branchId)
              and (:status is null or lower(so.status) = lower(:status))
              and (:fromTs is null or so.orderedAt >= :fromTs)
              and (:toTs is null or so.orderedAt <= :toTs)
            order by so.orderedAt desc, so.id desc
            """)
    List<SalesOrder> findCustomerOrders(@Param("storeId") Long storeId,
                                        @Param("customerId") Long customerId,
                                        @Param("branchId") Long branchId,
                                        @Param("status") String status,
                                        @Param("fromTs") LocalDateTime fromTs,
                                        @Param("toTs") LocalDateTime toTs);
}

