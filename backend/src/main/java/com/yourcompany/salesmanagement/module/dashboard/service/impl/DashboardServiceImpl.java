package com.yourcompany.salesmanagement.module.dashboard.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.module.dashboard.dto.response.DashboardSummaryResponse;
import com.yourcompany.salesmanagement.module.dashboard.service.DashboardService;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final NamedParameterJdbcTemplate jdbc;

    public DashboardServiceImpl(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public DashboardSummaryResponse getSummary(Long branchId) {
        Long storeId = SecurityUtils.requireStoreId();
        LocalDate today = LocalDate.now();

        var baseParams = new MapSqlParameterSource()
                .addValue("storeId", storeId)
                .addValue("branchId", branchId)
                .addValue("today", java.sql.Date.valueOf(today));

        String salesTodaySql = """
                select
                  coalesce(sum(gross_revenue), 0) as total_sales_today,
                  coalesce(sum(total_orders), 0) as total_orders_today
                from vw_sales_daily_summary
                where store_id = :storeId
                  and sale_date = :today
                  and (:branchId is null or branch_id = :branchId)
                """;

        var salesRow = jdbc.queryForMap(salesTodaySql, baseParams);
        BigDecimal totalSalesToday = (BigDecimal) salesRow.get("total_sales_today");
        Number totalOrdersToday = (Number) salesRow.get("total_orders_today");

        String customersSql = """
                select count(*) as total_customers
                from customers
                where store_id = :storeId
                """;
        Number totalCustomers = jdbc.queryForObject(customersSql,
                new MapSqlParameterSource().addValue("storeId", storeId), Number.class);

        String lowStockSql = """
                select count(*) as low_stock_products
                from vw_inventory_overview
                where store_id = :storeId
                  and (:branchId is null or branch_id = :branchId)
                  and available_quantity <= min_quantity
                """;
        Number lowStockProducts = jdbc.queryForObject(lowStockSql, baseParams, Number.class);

        return new DashboardSummaryResponse(
                totalSalesToday == null ? 0 : totalSalesToday.doubleValue(),
                totalOrdersToday == null ? 0 : totalOrdersToday.longValue(),
                totalCustomers == null ? 0 : totalCustomers.longValue(),
                lowStockProducts == null ? 0 : lowStockProducts.longValue()
        );
    }
}
