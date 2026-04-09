package com.yourcompany.salesmanagement.module.einvoice.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.auth.service.dto.UserPrincipal;
import com.yourcompany.salesmanagement.module.einvoice.dto.request.IssueEInvoiceRequest;
import com.yourcompany.salesmanagement.module.einvoice.dto.response.EInvoiceResponse;
import com.yourcompany.salesmanagement.module.einvoice.entity.EInvoice;
import com.yourcompany.salesmanagement.module.einvoice.repository.EInvoiceRepository;
import com.yourcompany.salesmanagement.module.einvoice.service.EInvoiceService;
import com.yourcompany.salesmanagement.module.salesorder.entity.SalesOrder;
import com.yourcompany.salesmanagement.module.salesorder.repository.SalesOrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EInvoiceServiceImpl implements EInvoiceService {
    private static final String STATUS_DRAFT = "DRAFT";
    private static final String STATUS_ISSUING = "ISSUING";
    private static final String STATUS_ISSUED = "ISSUED";
    private static final String STATUS_FAILED = "FAILED";

    private static final String PROVIDER_MOCK = "MOCK";

    private final EInvoiceRepository eInvoiceRepository;
    private final SalesOrderRepository salesOrderRepository;

    public EInvoiceServiceImpl(EInvoiceRepository eInvoiceRepository, SalesOrderRepository salesOrderRepository) {
        this.eInvoiceRepository = eInvoiceRepository;
        this.salesOrderRepository = salesOrderRepository;
    }

    @Override
    @Transactional
    public EInvoiceResponse issue(Long salesOrderId, IssueEInvoiceRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        UserPrincipal principal = SecurityUtils.requirePrincipal();

        if (salesOrderId == null) {
            throw new BusinessException("salesOrderId is required", HttpStatus.BAD_REQUEST);
        }

        SalesOrder so = salesOrderRepository.findByIdAndStoreId(salesOrderId, storeId)
                .orElseThrow(() -> new BusinessException("Sales order not found", HttpStatus.NOT_FOUND));

        // Foundation rule: only allow issuing for COMPLETED orders
        if (!"COMPLETED".equalsIgnoreCase(so.getStatus())) {
            throw new BusinessException("Only COMPLETED orders can be issued as e-invoice", HttpStatus.BAD_REQUEST);
        }

        // One e-invoice per order (unique key)
        EInvoice existing = eInvoiceRepository.findFirstByStoreIdAndSalesOrderIdOrderByIdDesc(storeId, salesOrderId).orElse(null);
        if (existing != null) {
            return toResponse(existing);
        }

        EInvoice ei = new EInvoice();
        ei.setStoreId(storeId);
        ei.setSalesOrderId(salesOrderId);
        ei.setStatus(STATUS_DRAFT);
        ei.setProviderName(PROVIDER_MOCK);

        if (request != null) {
            ei.setBuyerName(trimToNull(request.buyerName()));
            ei.setBuyerTaxCode(trimToNull(request.buyerTaxCode()));
            ei.setBuyerAddress(trimToNull(request.buyerAddress()));
            ei.setBuyerEmail(trimToNull(request.buyerEmail()));
        }

        ei.setSubtotal(money(so.getSubtotal()));
        ei.setTaxAmount(money(so.getTaxAmount()));
        ei.setTotalAmount(money(so.getTotalAmount()));
        ei.setCreatedBy(principal.userId());
        ei = eInvoiceRepository.save(ei);

        startMockIssueAsync(ei.getId(), storeId);
        return toResponse(ei);
    }

    @Override
    public EInvoiceResponse getById(Long id) {
        Long storeId = SecurityUtils.requireStoreId();
        EInvoice ei = eInvoiceRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new BusinessException("E-invoice not found", HttpStatus.NOT_FOUND));
        return toResponse(ei);
    }

    @Async
    @Transactional
    protected void startMockIssueAsync(Long eInvoiceId, Long storeId) {
        EInvoice ei = eInvoiceRepository.findForUpdateByIdAndStoreId(eInvoiceId, storeId).orElse(null);
        if (ei == null) return;

        if (STATUS_ISSUED.equalsIgnoreCase(ei.getStatus())) return;

        try {
            ei.setStatus(STATUS_ISSUING);
            eInvoiceRepository.save(ei);

            // Mock provider response: generate ids/numbers deterministically enough for FE display
            String providerInvoiceId = "MOCK-" + UUID.randomUUID().toString().replace("-", "").substring(0, 18);
            String invoiceNumber = "EINV-" + storeId + "-" + ei.getSalesOrderId();

            ei.setProviderInvoiceId(providerInvoiceId);
            ei.setInvoiceNumber(invoiceNumber);
            ei.setIssuedAt(LocalDateTime.now());
            ei.setStatus(STATUS_ISSUED);
            ei.setErrorMessage(null);
            eInvoiceRepository.save(ei);
        } catch (Exception ex) {
            ei.setStatus(STATUS_FAILED);
            String msg = ex.getMessage() == null ? "Issue failed" : ex.getMessage();
            ei.setErrorMessage(msg.substring(0, Math.min(1000, msg.length())));
            eInvoiceRepository.save(ei);
        }
    }

    private EInvoiceResponse toResponse(EInvoice e) {
        return new EInvoiceResponse(
                e.getId(),
                e.getStoreId(),
                e.getSalesOrderId(),
                e.getStatus(),
                e.getProviderName(),
                e.getProviderInvoiceId(),
                e.getInvoiceNumber(),
                e.getBuyerName(),
                e.getBuyerTaxCode(),
                e.getBuyerAddress(),
                e.getBuyerEmail(),
                e.getSubtotal(),
                e.getTaxAmount(),
                e.getTotalAmount(),
                e.getIssuedAt(),
                e.getErrorMessage(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    private BigDecimal money(BigDecimal v) {
        if (v == null) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return v.setScale(2, RoundingMode.HALF_UP);
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        String v = s.trim();
        return v.isEmpty() ? null : v;
    }
}

