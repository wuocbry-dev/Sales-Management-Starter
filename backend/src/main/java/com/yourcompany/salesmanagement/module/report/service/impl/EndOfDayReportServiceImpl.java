package com.yourcompany.salesmanagement.module.report.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.module.report.dto.response.EndOfDayCashbookSummary;
import com.yourcompany.salesmanagement.module.report.dto.response.EndOfDayPaymentMethodSummary;
import com.yourcompany.salesmanagement.module.report.dto.response.EndOfDayReportResponse;
import com.yourcompany.salesmanagement.module.report.service.EndOfDayReportService;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EndOfDayReportServiceImpl implements EndOfDayReportService {
    private final NamedParameterJdbcTemplate jdbc;

    public EndOfDayReportServiceImpl(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public EndOfDayReportResponse getEndOfDay(Long branchId, LocalDate date) {
        Long storeId = SecurityUtils.requireStoreId();
        LocalDate day = date == null ? LocalDate.now() : date;

        LocalDateTime from = day.atStartOfDay();
        LocalDateTime to = day.plusDays(1).atStartOfDay();

        var params = new MapSqlParameterSource()
                .addValue("storeId", storeId)
                .addValue("branchId", branchId)
                .addValue("fromTs", from)
                .addValue("toTs", to)
                .addValue("fromDate", Date.valueOf(day))
                .addValue("toDate", Date.valueOf(day.plusDays(1)));

        // Sales: completed orders within day by ordered_at
        String salesSql = """
                select
                  coalesce(sum(total_amount), 0) as gross_sales,
                  coalesce(count(*), 0) as total_orders
                from sales_orders
                where store_id = :storeId
                  and branch_id = :branchId
                  and status = 'COMPLETED'
                  and ordered_at >= :fromTs and ordered_at < :toTs
                """;
        var salesRow = jdbc.queryForMap(salesSql, params);
        BigDecimal grossSales = (BigDecimal) salesRow.get("gross_sales");
        Number totalOrders = (Number) salesRow.get("total_orders");

        // Refunds: return_orders created within day (refund_amount)
        String refundSql = """
                select
                  coalesce(sum(refund_amount), 0) as refunds
                from return_orders
                where store_id = :storeId
                  and branch_id = :branchId
                  and created_at >= :fromTs and created_at < :toTs
                """;
        BigDecimal refunds = jdbc.queryForObject(refundSql, params, BigDecimal.class);
        if (refunds == null) refunds = BigDecimal.ZERO;

        BigDecimal netSales = (grossSales == null ? BigDecimal.ZERO : grossSales).subtract(refunds).max(BigDecimal.ZERO);

        // Payments by method: sum(amount) within day by paid_at
        String paymentByMethodSql = """
                select
                  payment_method,
                  coalesce(sum(amount), 0) as amount
                from payments
                where store_id = :storeId
                  and branch_id = :branchId
                  and paid_at >= :fromTs and paid_at < :toTs
                group by payment_method
                order by payment_method asc
                """;
        List<EndOfDayPaymentMethodSummary> byMethod = jdbc.query(paymentByMethodSql, params,
                (rs, rowNum) -> new EndOfDayPaymentMethodSummary(
                        rs.getString("payment_method"),
                        rs.getBigDecimal("amount") == null ? BigDecimal.ZERO : rs.getBigDecimal("amount")
                ));

        // Cashbook totals in/out within day by occurred_at (branch_id may be null for store-wide entries)
        String cashbookSql = """
                select
                  coalesce(sum(case when entry_type = 'IN' then amount else 0 end), 0) as total_in,
                  coalesce(sum(case when entry_type = 'OUT' then amount else 0 end), 0) as total_out
                from cashbook_entries
                where store_id = :storeId
                  and (branch_id = :branchId or branch_id is null)
                  and occurred_at >= :fromTs and occurred_at < :toTs
                """;
        var cashRow = jdbc.queryForMap(cashbookSql, params);
        BigDecimal totalIn = (BigDecimal) cashRow.get("total_in");
        BigDecimal totalOut = (BigDecimal) cashRow.get("total_out");

        return new EndOfDayReportResponse(
                storeId,
                branchId,
                day,
                grossSales == null ? BigDecimal.ZERO : grossSales,
                refunds,
                netSales,
                totalOrders == null ? 0L : totalOrders.longValue(),
                byMethod,
                new EndOfDayCashbookSummary(
                        totalIn == null ? BigDecimal.ZERO : totalIn,
                        totalOut == null ? BigDecimal.ZERO : totalOut
                )
        );
    }
}

