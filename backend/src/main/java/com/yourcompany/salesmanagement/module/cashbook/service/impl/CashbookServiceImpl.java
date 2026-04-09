package com.yourcompany.salesmanagement.module.cashbook.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.auth.service.dto.UserPrincipal;
import com.yourcompany.salesmanagement.module.cashbook.dto.request.CreateCashbookEntryRequest;
import com.yourcompany.salesmanagement.module.cashbook.dto.response.CashbookEntryResponse;
import com.yourcompany.salesmanagement.module.cashbook.dto.response.CashbookSummaryResponse;
import com.yourcompany.salesmanagement.module.cashbook.entity.CashbookEntry;
import com.yourcompany.salesmanagement.module.cashbook.repository.CashbookEntryRepository;
import com.yourcompany.salesmanagement.module.cashbook.service.CashbookService;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CashbookServiceImpl implements CashbookService {
    private final CashbookEntryRepository cashbookEntryRepository;
    private final NamedParameterJdbcTemplate jdbc;

    public CashbookServiceImpl(CashbookEntryRepository cashbookEntryRepository, NamedParameterJdbcTemplate jdbc) {
        this.cashbookEntryRepository = cashbookEntryRepository;
        this.jdbc = jdbc;
    }

    @Override
    public List<CashbookEntryResponse> list() {
        Long storeId = SecurityUtils.requireStoreId();
        return cashbookEntryRepository.findAllByStoreIdOrderByOccurredAtDescIdDesc(storeId).stream()
                .map(e -> new CashbookEntryResponse(
                        e.getId(),
                        e.getStoreId(),
                        e.getBranchId(),
                        e.getEntryType(),
                        e.getCategory(),
                        e.getReferenceType(),
                        e.getReferenceId(),
                        e.getAmount(),
                        e.getDescription(),
                        e.getOccurredAt()
                ))
                .toList();
    }

    @Override
    @Transactional
    public CashbookEntryResponse create(CreateCashbookEntryRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        UserPrincipal principal = SecurityUtils.requirePrincipal();

        String entryType = request.entryType() == null ? null : request.entryType().trim().toUpperCase();
        if (!"IN".equals(entryType) && !"OUT".equals(entryType)) {
            throw new BusinessException("entryType must be IN or OUT", HttpStatus.BAD_REQUEST);
        }

        CashbookEntry e = new CashbookEntry();
        e.setStoreId(storeId);
        e.setBranchId(request.branchId());
        e.setEntryType(entryType);
        e.setCategory(request.category().trim());
        e.setReferenceType(request.referenceType() == null ? null : request.referenceType().trim());
        e.setReferenceId(request.referenceId());
        e.setAmount(money(request.amount()));
        e.setDescription(request.description() == null ? null : request.description().trim());
        e.setOccurredAt(request.occurredAt() == null ? LocalDateTime.now() : request.occurredAt());
        e.setCreatedBy(principal.userId());
        e = cashbookEntryRepository.save(e);
        return new CashbookEntryResponse(
                e.getId(),
                e.getStoreId(),
                e.getBranchId(),
                e.getEntryType(),
                e.getCategory(),
                e.getReferenceType(),
                e.getReferenceId(),
                e.getAmount(),
                e.getDescription(),
                e.getOccurredAt()
        );
    }

    @Override
    public CashbookSummaryResponse getSummary(Long branchId, LocalDate date) {
        Long storeId = SecurityUtils.requireStoreId();
        if (branchId == null) {
            throw new BusinessException("branchId is required", HttpStatus.BAD_REQUEST);
        }
        LocalDate day = date == null ? LocalDate.now() : date;
        LocalDateTime from = day.atStartOfDay();
        LocalDateTime to = day.plusDays(1).atStartOfDay();

        var params = new MapSqlParameterSource()
                .addValue("storeId", storeId)
                .addValue("branchId", branchId)
                .addValue("fromTs", from)
                .addValue("toTs", to);

        String sql = """
                select
                  coalesce(sum(case when entry_type = 'IN' then amount else 0 end), 0) as total_in,
                  coalesce(sum(case when entry_type = 'OUT' then amount else 0 end), 0) as total_out
                from cashbook_entries
                where store_id = :storeId
                  and (branch_id = :branchId or branch_id is null)
                  and occurred_at >= :fromTs and occurred_at < :toTs
                """;
        var row = jdbc.queryForMap(sql, params);
        BigDecimal totalIn = (BigDecimal) row.get("total_in");
        BigDecimal totalOut = (BigDecimal) row.get("total_out");
        if (totalIn == null) totalIn = BigDecimal.ZERO;
        if (totalOut == null) totalOut = BigDecimal.ZERO;
        BigDecimal net = totalIn.subtract(totalOut).setScale(2, RoundingMode.HALF_UP);
        return new CashbookSummaryResponse(storeId, branchId, day, money(totalIn), money(totalOut), money(net));
    }

    private BigDecimal money(BigDecimal v) {
        if (v == null) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return v.setScale(2, RoundingMode.HALF_UP);
    }
}

