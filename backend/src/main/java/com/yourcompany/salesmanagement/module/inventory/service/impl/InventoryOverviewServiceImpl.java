package com.yourcompany.salesmanagement.module.inventory.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.module.inventory.dto.response.InventoryOverviewResponse;
import com.yourcompany.salesmanagement.module.inventory.service.InventoryOverviewService;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryOverviewServiceImpl implements InventoryOverviewService {
    private final NamedParameterJdbcTemplate jdbc;

    public InventoryOverviewServiceImpl(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<InventoryOverviewResponse> getOverviewByBranch(Long branchId) {
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
                where store_id = :storeId and branch_id = :branchId
                order by product_name asc, variant_name asc
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

