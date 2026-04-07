package com.yourcompany.salesmanagement.module.dashboard.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.module.dashboard.dto.response.SalesDailySummaryResponse;
import com.yourcompany.salesmanagement.module.dashboard.service.DashboardReportService;
import com.yourcompany.salesmanagement.module.inventory.dto.response.InventoryOverviewResponse;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardReportServiceImpl implements DashboardReportService {
    private final NamedParameterJdbcTemplate jdbc;

    public DashboardReportServiceImpl(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<SalesDailySummaryResponse> getSalesDailySummary(Long branchId, LocalDate fromDate, LocalDate toDate) {
        Long storeId = SecurityUtils.requireStoreId();
        String sql = """
                select
                  store_id,
                  branch_id,
                  sale_date,
                  total_orders,
                  gross_revenue,
                  collected_amount
                from vw_sales_daily_summary
                where store_id = :storeId
                  and (:branchId is null or branch_id = :branchId)
                  and (:fromDate is null or sale_date >= :fromDate)
                  and (:toDate is null or sale_date <= :toDate)
                order by sale_date desc
                """;

        var params = new MapSqlParameterSource()
                .addValue("storeId", storeId)
                .addValue("branchId", branchId)
                .addValue("fromDate", fromDate == null ? null : Date.valueOf(fromDate))
                .addValue("toDate", toDate == null ? null : Date.valueOf(toDate));

        return jdbc.query(sql, params, (rs, rowNum) -> new SalesDailySummaryResponse(
                rs.getLong("store_id"),
                rs.getLong("branch_id"),
                rs.getDate("sale_date").toLocalDate(),
                rs.getLong("total_orders"),
                rs.getBigDecimal("gross_revenue") == null ? BigDecimal.ZERO : rs.getBigDecimal("gross_revenue"),
                rs.getBigDecimal("collected_amount") == null ? BigDecimal.ZERO : rs.getBigDecimal("collected_amount")
        ));
    }

    @Override
    public List<InventoryOverviewResponse> getInventoryWarnings(Long branchId) {
        Long storeId = SecurityUtils.requireStoreId();
        String sql = """
                select
                  store_id,
                  branch_id,
                  product_id,
                  sku,
                  product_name,
                  variant_name,
                  quantity,
                  reserved_quantity,
                  available_quantity,
                  min_quantity,
                  max_quantity
                from vw_inventory_overview
                where store_id = :storeId
                  and branch_id = :branchId
                  and available_quantity <= min_quantity
                order by available_quantity asc, product_name asc, variant_name asc
                """;

        var params = new MapSqlParameterSource()
                .addValue("storeId", storeId)
                .addValue("branchId", branchId);

        return jdbc.query(sql, params, (rs, rowNum) -> new InventoryOverviewResponse(
                rs.getLong("store_id"),
                rs.getLong("branch_id"),
                rs.getLong("product_id"),
                rs.getString("sku"),
                rs.getString("product_name"),
                rs.getString("variant_name"),
                rs.getBigDecimal("quantity"),
                rs.getBigDecimal("reserved_quantity"),
                rs.getBigDecimal("available_quantity"),
                rs.getBigDecimal("min_quantity"),
                rs.getBigDecimal("max_quantity")
        ));
    }
}

