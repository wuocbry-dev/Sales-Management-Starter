package com.yourcompany.salesmanagement.module.customer.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.customer.dto.request.CreateCustomerRequest;
import com.yourcompany.salesmanagement.module.customer.dto.request.UpdateCustomerRequest;
import com.yourcompany.salesmanagement.module.customer.dto.response.CustomerResponse;
import com.yourcompany.salesmanagement.module.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/customers", "/api/customers"})
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public BaseResponse<List<CustomerResponse>> search(@RequestParam(required = false) String query) {
        return BaseResponse.ok("Customers fetched successfully", customerService.search(query));
    }

    @GetMapping("/{id}")
    public BaseResponse<CustomerResponse> getById(@PathVariable Long id) {
        return BaseResponse.ok("Customer fetched successfully", customerService.getById(id));
    }

    @PostMapping
    public BaseResponse<CustomerResponse> create(@Valid @RequestBody CreateCustomerRequest request) {
        return BaseResponse.ok("Customer created successfully", customerService.create(request));
    }

    @PutMapping("/{id}")
    public BaseResponse<CustomerResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateCustomerRequest request) {
        return BaseResponse.ok("Customer updated successfully", customerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return BaseResponse.ok("Customer deleted successfully", null);
    }
}

