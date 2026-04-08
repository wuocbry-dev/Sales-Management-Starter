package com.yourcompany.salesmanagement.module.dashboard.repository;

import com.yourcompany.salesmanagement.module.dashboard.dto.response.SalesDailySummaryResponse;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

@NoRepositoryBean
public interface SalesDailySummaryRepository extends Repository<Object, Long> {
    @Query(value = """
            select
              store_id as storeId,
              branch_id as branchId,
              sale_date as saleDate,
              total_orders as totalOrders,
              gross_revenue as grossRevenue,
              collected_amount as collectedAmount
            from vw_sales_daily_summary
            where store_id = :storeId
              and (:branchId is null or branch_id = :branchId)
              and (:fromDate is null or sale_date >= :fromDate)
              and (:toDate is null or sale_date <= :toDate)
            order by sale_date desc
            """, nativeQuery = true)
    List<SalesDailySummaryResponse> findByFilter(
            @Param("storeId") Long storeId,
            @Param("branchId") Long branchId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );
}

