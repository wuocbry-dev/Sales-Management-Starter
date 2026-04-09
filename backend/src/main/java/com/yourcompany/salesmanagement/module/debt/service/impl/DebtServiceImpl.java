package com.yourcompany.salesmanagement.module.debt.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.module.debt.dto.response.CustomerDebtResponse;
import com.yourcompany.salesmanagement.module.debt.dto.response.SupplierDebtResponse;
import com.yourcompany.salesmanagement.module.debt.service.DebtService;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DebtServiceImpl implements DebtService {
    private final NamedParameterJdbcTemplate jdbc;

    public DebtServiceImpl(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<CustomerDebtResponse> listCustomerDebts(Long branchId) {
        Long storeId = SecurityUtils.requireStoreId();
        var params = new MapSqlParameterSource()
                .addValue("storeId", storeId)
                .addValue("branchId", branchId);

        // MVP definition:
        // - Only COMPLETED sales orders contribute to AR
        // - Debt per order = max(total_amount - paid_amount, 0)
        // - Group by customer
        String sql = """
                select
                  c.id as customer_id,
                  c.customer_code,
                  c.full_name,
                  c.phone,
                  c.last_order_at,
                  coalesce(sum(greatest(so.total_amount - so.paid_amount, 0)), 0) as total_debt,
                  coalesce(sum(case when so.total_amount > so.paid_amount then 1 else 0 end), 0) as open_orders_count
                from customers c
                join sales_orders so
                  on so.store_id = c.store_id
                 and so.customer_id = c.id
                where c.store_id = :storeId
                  and (:branchId is null or so.branch_id = :branchId)
                  and so.status = 'COMPLETED'
                group by c.id, c.customer_code, c.full_name, c.phone, c.last_order_at
                having coalesce(sum(greatest(so.total_amount - so.paid_amount, 0)), 0) > 0
                order by total_debt desc, c.id desc
                """;

        return jdbc.query(sql, params, (rs, rowNum) -> new CustomerDebtResponse(
                rs.getLong("customer_id"),
                rs.getString("customer_code"),
                rs.getString("full_name"),
                rs.getString("phone"),
                rs.getBigDecimal("total_debt") == null ? BigDecimal.ZERO : rs.getBigDecimal("total_debt"),
                rs.getLong("open_orders_count"),
                rs.getTimestamp("last_order_at") == null ? null : rs.getTimestamp("last_order_at").toLocalDateTime()
        ));
    }

    @Override
    public List<SupplierDebtResponse> listSupplierDebts(Long branchId) {
        Long storeId = SecurityUtils.requireStoreId();
        var params = new MapSqlParameterSource()
                .addValue("storeId", storeId)
                .addValue("branchId", branchId);

        // MVP definition:
        // - Payable per supplier is total purchase_orders.total_amount (excluding CANCELLED)
        // - Paid per supplier is cashbook OUT entries with category='SUPPLIER_PAYMENT'
        //   and reference_type='supplier' and reference_id = supplier.id
        String sql = """
                with po as (
                  select
                    supplier_id,
                    coalesce(sum(total_amount), 0) as total_payable
                  from purchase_orders
                  where store_id = :storeId
                    and (:branchId is null or branch_id = :branchId)
                    and status <> 'CANCELLED'
                  group by supplier_id
                ),
                paid as (
                  select
                    reference_id as supplier_id,
                    coalesce(sum(amount), 0) as total_paid
                  from cashbook_entries
                  where store_id = :storeId
                    and entry_type = 'OUT'
                    and category = 'SUPPLIER_PAYMENT'
                    and reference_type = 'supplier'
                    and reference_id is not null
                    and (:branchId is null or branch_id = :branchId or branch_id is null)
                  group by reference_id
                )
                select
                  s.id as supplier_id,
                  s.name,
                  s.phone,
                  coalesce(po.total_payable, 0) as total_payable,
                  coalesce(paid.total_paid, 0) as total_paid,
                  greatest(coalesce(po.total_payable, 0) - coalesce(paid.total_paid, 0), 0) as remaining_payable
                from suppliers s
                left join po on po.supplier_id = s.id
                left join paid on paid.supplier_id = s.id
                where s.store_id = :storeId
                  and coalesce(po.total_payable, 0) > 0
                order by remaining_payable desc, s.id desc
                """;

        return jdbc.query(sql, params, (rs, rowNum) -> new SupplierDebtResponse(
                rs.getLong("supplier_id"),
                rs.getString("name"),
                rs.getString("phone"),
                rs.getBigDecimal("total_payable") == null ? BigDecimal.ZERO : rs.getBigDecimal("total_payable"),
                rs.getBigDecimal("total_paid") == null ? BigDecimal.ZERO : rs.getBigDecimal("total_paid"),
                rs.getBigDecimal("remaining_payable") == null ? BigDecimal.ZERO : rs.getBigDecimal("remaining_payable")
        ));
    }
}

