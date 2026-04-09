package com.yourcompany.salesmanagement.module.customer.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.customer.dto.request.CreateCustomerRequest;
import com.yourcompany.salesmanagement.module.customer.dto.request.UpdateCustomerRequest;
import com.yourcompany.salesmanagement.module.customer.dto.response.CustomerResponse;
import com.yourcompany.salesmanagement.module.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping({"/api/v1/customers", "/api/customers"})
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CUSTOMER_READ') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<List<CustomerResponse>> search(@RequestParam(required = false) String query) {
        return BaseResponse.ok("Customers fetched successfully", customerService.search(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER_READ') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<CustomerResponse> getById(@PathVariable Long id) {
        return BaseResponse.ok("Customer fetched successfully", customerService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CUSTOMER_WRITE') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<CustomerResponse> create(@Valid @RequestBody CreateCustomerRequest request) {
        return BaseResponse.ok("Customer created successfully", customerService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER_WRITE') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<CustomerResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateCustomerRequest request) {
        return BaseResponse.ok("Customer updated successfully", customerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER_WRITE') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return BaseResponse.ok("Customer deleted successfully", null);
    }

    @GetMapping("/{id}/orders")
    @PreAuthorize("hasAuthority('CUSTOMER_READ') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<List<com.yourcompany.salesmanagement.module.salesorder.dto.response.SalesOrderSummaryResponse>> orders(
            @PathVariable Long id,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to
    ) {
        return BaseResponse.ok("Customer orders fetched successfully",
                customerService.getCustomerOrders(id, branchId, status, from, to));
    }
}

