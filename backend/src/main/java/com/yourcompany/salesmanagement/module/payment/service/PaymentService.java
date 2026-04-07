package com.yourcompany.salesmanagement.module.payment.service;

import com.yourcompany.salesmanagement.module.payment.dto.request.CreatePaymentRequest;
import com.yourcompany.salesmanagement.module.payment.dto.response.OrderPaymentStatusResponse;
import com.yourcompany.salesmanagement.module.payment.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {
    PaymentResponse createPayment(CreatePaymentRequest request);

    List<PaymentResponse> getPaymentsBySalesOrderId(Long salesOrderId);

    OrderPaymentStatusResponse getOrderPaymentStatus(Long salesOrderId);
}

