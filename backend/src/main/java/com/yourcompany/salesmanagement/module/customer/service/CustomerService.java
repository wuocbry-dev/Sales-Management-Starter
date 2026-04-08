package com.yourcompany.salesmanagement.module.customer.service;

import com.yourcompany.salesmanagement.module.customer.dto.request.CreateCustomerRequest;
import com.yourcompany.salesmanagement.module.customer.dto.request.UpdateCustomerRequest;
import com.yourcompany.salesmanagement.module.customer.dto.response.CustomerResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface CustomerService {
    List<CustomerResponse> search(String query);

    CustomerResponse getById(Long id);

    CustomerResponse create(CreateCustomerRequest request);

    CustomerResponse update(Long id, UpdateCustomerRequest request);

    void delete(Long id);

    void applyPurchase(Long customerId, BigDecimal amount, int earnedPoints, LocalDateTime orderedAt);
}

