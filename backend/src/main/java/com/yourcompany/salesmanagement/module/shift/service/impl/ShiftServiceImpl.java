package com.yourcompany.salesmanagement.module.shift.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.shift.dto.request.CloseShiftRequest;
import com.yourcompany.salesmanagement.module.shift.dto.request.OpenShiftRequest;
import com.yourcompany.salesmanagement.module.shift.dto.response.ShiftResponse;
import com.yourcompany.salesmanagement.module.shift.dto.response.ShiftSummaryResponse;
import com.yourcompany.salesmanagement.module.shift.entity.Shift;
import com.yourcompany.salesmanagement.module.shift.repository.ShiftRepository;
import com.yourcompany.salesmanagement.module.shift.service.ShiftService;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShiftServiceImpl implements ShiftService {
    private final ShiftRepository shiftRepository;
    private final NamedParameterJdbcTemplate jdbc;

    public ShiftServiceImpl(ShiftRepository shiftRepository, NamedParameterJdbcTemplate jdbc) {
        this.shiftRepository = shiftRepository;
        this.jdbc = jdbc;
    }

    @Override
    @Transactional
    public ShiftResponse open(OpenShiftRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        Long userId = SecurityUtils.requirePrincipal().userId();
        Long branchId = request.branchId();

        shiftRepository.findFirstByStoreIdAndBranchIdAndStatusOrderByOpenedAtDesc(storeId, branchId, Shift.STATUS_OPEN)
                .ifPresent(s -> {
                    throw new BusinessException("This branch already has an open shift", HttpStatus.CONFLICT);
                });

        Shift s = new Shift();
        s.setStoreId(storeId);
        s.setBranchId(branchId);
        s.setCashierUserId(userId);
        s.setStatus(Shift.STATUS_OPEN);
        s.setOpeningCash(request.openingCash());
        s.setNote(request.note());
        s = shiftRepository.save(s);
        return toResponse(s);
    }

    @Override
    @Transactional
    public ShiftResponse close(Long shiftId, CloseShiftRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        Shift s = shiftRepository.findByIdAndStoreId(shiftId, storeId)
                .orElseThrow(() -> new BusinessException("Shift not found", HttpStatus.NOT_FOUND));

        if (!Shift.STATUS_OPEN.equalsIgnoreCase(s.getStatus())) {
            throw new BusinessException("Shift is not open", HttpStatus.BAD_REQUEST);
        }

        s.setClosingCash(request.closingCash());
        s.setClosedAt(LocalDateTime.now());
        s.setStatus(Shift.STATUS_CLOSED);
        if (request.note() != null && !request.note().isBlank()) {
            s.setNote(request.note().trim());
        }
        s = shiftRepository.save(s);
        return toResponse(s);
    }

    @Override
    public ShiftResponse getCurrent(Long branchId) {
        Long storeId = SecurityUtils.requireStoreId();
        Shift s = shiftRepository.findFirstByStoreIdAndBranchIdAndStatusOrderByOpenedAtDesc(storeId, branchId, Shift.STATUS_OPEN)
                .orElseThrow(() -> new BusinessException("No open shift for this branch", HttpStatus.NOT_FOUND));
        return toResponse(s);
    }

    @Override
    public ShiftSummaryResponse getSummary(Long shiftId) {
        Long storeId = SecurityUtils.requireStoreId();
        Shift shift = shiftRepository.findByIdAndStoreId(shiftId, storeId)
                .orElseThrow(() -> new BusinessException("Shift not found", HttpStatus.NOT_FOUND));

        LocalDateTime from = shift.getOpenedAt();
        LocalDateTime to = shift.getClosedAt() == null ? LocalDateTime.now() : shift.getClosedAt();

        var params = new MapSqlParameterSource()
                .addValue("storeId", storeId)
                .addValue("branchId", shift.getBranchId())
                .addValue("fromTs", from)
                .addValue("toTs", to);

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

        String refundSql = """
                select coalesce(sum(refund_amount), 0) as refunds
                from return_orders
                where store_id = :storeId
                  and branch_id = :branchId
                  and created_at >= :fromTs and created_at < :toTs
                """;
        BigDecimal refunds = jdbc.queryForObject(refundSql, params, BigDecimal.class);
        if (refunds == null) refunds = BigDecimal.ZERO;

        BigDecimal netSales = (grossSales == null ? BigDecimal.ZERO : grossSales).subtract(refunds).max(BigDecimal.ZERO);

        String paymentByMethodSql = """
                select payment_method, coalesce(sum(amount), 0) as amount
                from payments
                where store_id = :storeId
                  and branch_id = :branchId
                  and paid_at >= :fromTs and paid_at < :toTs
                group by payment_method
                order by payment_method asc
                """;
        List<ShiftSummaryResponse.PaymentMethodAmount> byMethod = jdbc.query(paymentByMethodSql, params,
                (rs, rowNum) -> new ShiftSummaryResponse.PaymentMethodAmount(
                        rs.getString("payment_method"),
                        rs.getBigDecimal("amount") == null ? BigDecimal.ZERO : rs.getBigDecimal("amount")
                ));

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
        BigDecimal cashIn = (BigDecimal) cashRow.get("total_in");
        BigDecimal cashOut = (BigDecimal) cashRow.get("total_out");

        return new ShiftSummaryResponse(
                shift.getId(),
                storeId,
                shift.getBranchId(),
                from,
                to,
                totalOrders == null ? 0L : totalOrders.longValue(),
                grossSales == null ? BigDecimal.ZERO : grossSales,
                refunds,
                netSales,
                byMethod,
                cashIn == null ? BigDecimal.ZERO : cashIn,
                cashOut == null ? BigDecimal.ZERO : cashOut
        );
    }

    private ShiftResponse toResponse(Shift s) {
        return new ShiftResponse(
                s.getId(),
                s.getStoreId(),
                s.getBranchId(),
                s.getCashierUserId(),
                s.getStatus(),
                s.getOpeningCash(),
                s.getClosingCash(),
                s.getOpenedAt(),
                s.getClosedAt(),
                s.getNote()
        );
    }
}

