package com.yourcompany.salesmanagement.module.payment.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.auth.service.dto.UserPrincipal;
import com.yourcompany.salesmanagement.module.payment.dto.request.CreatePaymentRequest;
import com.yourcompany.salesmanagement.module.payment.dto.response.OrderPaymentStatusResponse;
import com.yourcompany.salesmanagement.module.payment.dto.response.PaymentResponse;
import com.yourcompany.salesmanagement.module.payment.entity.Payment;
import com.yourcompany.salesmanagement.module.payment.repository.PaymentRepository;
import com.yourcompany.salesmanagement.module.salesorder.entity.SalesOrder;
import com.yourcompany.salesmanagement.module.salesorder.repository.SalesOrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements com.yourcompany.salesmanagement.module.payment.service.PaymentService {

    private static final String STATUS_PAID = "PAID";

    private final PaymentRepository paymentRepository;
    private final SalesOrderRepository salesOrderRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository, SalesOrderRepository salesOrderRepository) {
        this.paymentRepository = paymentRepository;
        this.salesOrderRepository = salesOrderRepository;
    }

    @Override
    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        UserPrincipal principal = SecurityUtils.requirePrincipal();

        SalesOrder so = salesOrderRepository.findForUpdateByIdAndStoreId(request.salesOrderId(), storeId)
                .orElseThrow(() -> new BusinessException("Sales order not found", HttpStatus.NOT_FOUND));

        BigDecimal total = money(so.getTotalAmount());
        BigDecimal paid = money(so.getPaidAmount());
        BigDecimal remaining = total.subtract(paid).setScale(2, RoundingMode.HALF_UP);

        BigDecimal amount = money(request.amount());
        if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Order is already fully paid", HttpStatus.BAD_REQUEST);
        }
        if (amount.compareTo(remaining) > 0) {
            throw new BusinessException("Payment amount exceeds remaining amount: " + remaining, HttpStatus.BAD_REQUEST);
        }

        Payment p = new Payment();
        p.setStoreId(storeId);
        p.setBranchId(so.getBranchId());
        p.setSalesOrderId(so.getId());
        p.setPaymentCode(generatePaymentCode(storeId));
        p.setPaymentMethod(request.paymentMethod().trim());
        p.setStatus(STATUS_PAID);
        p.setAmount(amount);
        p.setPaidAt(request.paidAt() != null ? request.paidAt() : LocalDateTime.now());
        p.setTransactionRef(request.transactionRef());
        p.setNotes(request.notes());
        p.setCreatedBy(principal.userId());
        p = paymentRepository.save(p);

        so.setPaidAmount(paid.add(amount).setScale(2, RoundingMode.HALF_UP));
        salesOrderRepository.save(so);

        return toResponse(p);
    }

    @Override
    public List<PaymentResponse> getPaymentsBySalesOrderId(Long salesOrderId) {
        Long storeId = SecurityUtils.requireStoreId();
        // Ensure order belongs to store
        salesOrderRepository.findByIdAndStoreId(salesOrderId, storeId)
                .orElseThrow(() -> new BusinessException("Sales order not found", HttpStatus.NOT_FOUND));

        return paymentRepository.findAllByStoreIdAndSalesOrderIdOrderByIdDesc(storeId, salesOrderId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public OrderPaymentStatusResponse getOrderPaymentStatus(Long salesOrderId) {
        Long storeId = SecurityUtils.requireStoreId();
        SalesOrder so = salesOrderRepository.findByIdAndStoreId(salesOrderId, storeId)
                .orElseThrow(() -> new BusinessException("Sales order not found", HttpStatus.NOT_FOUND));

        BigDecimal total = money(so.getTotalAmount());
        BigDecimal paid = money(so.getPaidAmount());
        BigDecimal remaining = total.subtract(paid).setScale(2, RoundingMode.HALF_UP);

        String status;
        if (paid.compareTo(BigDecimal.ZERO) <= 0) status = "UNPAID";
        else if (remaining.compareTo(BigDecimal.ZERO) <= 0) status = "PAID";
        else status = "PARTIALLY_PAID";

        return new OrderPaymentStatusResponse(so.getId(), total, paid, remaining, status);
    }

    private PaymentResponse toResponse(Payment p) {
        return new PaymentResponse(
                p.getId(),
                p.getSalesOrderId(),
                p.getPaymentCode(),
                p.getPaymentMethod(),
                p.getStatus(),
                p.getAmount(),
                p.getPaidAt(),
                p.getTransactionRef(),
                p.getNotes()
        );
    }

    private BigDecimal money(BigDecimal v) {
        if (v == null) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return v.setScale(2, RoundingMode.HALF_UP);
    }

    private String generatePaymentCode(Long storeId) {
        return "PAY-" + storeId + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}

