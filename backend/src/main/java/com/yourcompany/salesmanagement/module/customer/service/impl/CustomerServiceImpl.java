package com.yourcompany.salesmanagement.module.customer.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.customer.dto.request.CreateCustomerRequest;
import com.yourcompany.salesmanagement.module.customer.dto.request.UpdateCustomerRequest;
import com.yourcompany.salesmanagement.module.customer.dto.response.CustomerResponse;
import com.yourcompany.salesmanagement.module.customer.entity.Customer;
import com.yourcompany.salesmanagement.module.customer.repository.CustomerRepository;
import com.yourcompany.salesmanagement.module.customer.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<CustomerResponse> search(String query) {
        Long storeId = SecurityUtils.requireStoreId();
        return customerRepository.search(storeId, query).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public CustomerResponse getById(Long id) {
        Long storeId = SecurityUtils.requireStoreId();
        Customer c = customerRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new BusinessException("Customer not found", HttpStatus.NOT_FOUND));
        return toResponse(c);
    }

    @Override
    public CustomerResponse create(CreateCustomerRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        if (customerRepository.existsByStoreIdAndCustomerCode(storeId, request.customerCode())) {
            throw new BusinessException("Customer code already exists", HttpStatus.CONFLICT);
        }
        Customer c = new Customer();
        c.setStoreId(storeId);
        c.setCustomerCode(request.customerCode().trim());
        c.setFullName(request.fullName().trim());
        c.setPhone(request.phone());
        c.setEmail(request.email());
        c.setGender(request.gender());
        c.setDateOfBirth(request.dateOfBirth());
        c.setAddress(request.address());
        c.setTotalPoints(0);
        c.setTotalSpent(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        c.setStatus("ACTIVE");
        c = customerRepository.save(c);
        return toResponse(c);
    }

    @Override
    public CustomerResponse update(Long id, UpdateCustomerRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        Customer c = customerRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new BusinessException("Customer not found", HttpStatus.NOT_FOUND));
        c.setFullName(request.fullName().trim());
        c.setPhone(request.phone());
        c.setEmail(request.email());
        c.setGender(request.gender());
        c.setDateOfBirth(request.dateOfBirth());
        c.setAddress(request.address());
        if (request.status() != null && !request.status().isBlank()) {
            c.setStatus(request.status().trim());
        }
        c = customerRepository.save(c);
        return toResponse(c);
    }

    @Override
    @Transactional
    public void applyPurchase(Long customerId, BigDecimal amount, int earnedPoints, LocalDateTime orderedAt) {
        Long storeId = SecurityUtils.requireStoreId();
        Customer c = customerRepository.findByIdAndStoreId(customerId, storeId)
                .orElseThrow(() -> new BusinessException("Customer not found", HttpStatus.NOT_FOUND));

        BigDecimal delta = money(amount);
        c.setTotalSpent(money(c.getTotalSpent()).add(delta).setScale(2, RoundingMode.HALF_UP));
        c.setLastOrderAt(orderedAt);
        c.setTotalPoints((c.getTotalPoints() == null ? 0 : c.getTotalPoints()) + Math.max(0, earnedPoints));
        customerRepository.save(c);
    }

    private BigDecimal money(BigDecimal v) {
        if (v == null) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return v.setScale(2, RoundingMode.HALF_UP);
    }

    private CustomerResponse toResponse(Customer c) {
        return new CustomerResponse(
                c.getId(),
                c.getCustomerCode(),
                c.getFullName(),
                c.getPhone(),
                c.getEmail(),
                c.getGender(),
                c.getDateOfBirth(),
                c.getAddress(),
                c.getTotalPoints(),
                c.getTotalSpent(),
                c.getLastOrderAt(),
                c.getStatus()
        );
    }
}

