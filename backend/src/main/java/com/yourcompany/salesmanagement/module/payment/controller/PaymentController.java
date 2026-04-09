package com.yourcompany.salesmanagement.module.payment.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.payment.dto.request.CreatePaymentRequest;
import com.yourcompany.salesmanagement.module.payment.dto.response.OrderPaymentStatusResponse;
import com.yourcompany.salesmanagement.module.payment.dto.response.PaymentResponse;
import com.yourcompany.salesmanagement.module.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/payments", "/api/payments"})
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PAYMENT_CREATE') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<PaymentResponse> create(@Valid @RequestBody CreatePaymentRequest request) {
        return BaseResponse.ok("Payment created successfully", paymentService.createPayment(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PAYMENT_READ') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<PaymentResponse> getById(@PathVariable Long id) {
        return BaseResponse.ok("Payment fetched successfully", paymentService.getById(id));
    }

    @GetMapping("/order/{salesOrderId}")
    @PreAuthorize("hasAuthority('PAYMENT_READ') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<List<PaymentResponse>> listBySalesOrder(@PathVariable Long salesOrderId) {
        return BaseResponse.ok("Payments fetched successfully", paymentService.getPaymentsBySalesOrderId(salesOrderId));
    }

    @GetMapping("/status")
    @PreAuthorize("hasAuthority('PAYMENT_READ') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<OrderPaymentStatusResponse> orderPaymentStatus(@RequestParam Long salesOrderId) {
        return BaseResponse.ok("OK", paymentService.getOrderPaymentStatus(salesOrderId));
    }
}

