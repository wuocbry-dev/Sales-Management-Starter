package com.yourcompany.salesmanagement.module.employee.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.employee.dto.request.CreateEmployeeRequest;
import com.yourcompany.salesmanagement.module.employee.dto.request.UpdateEmployeeRequest;
import com.yourcompany.salesmanagement.module.employee.dto.response.EmployeeResponse;
import com.yourcompany.salesmanagement.module.employee.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/employees", "/api/employees"})
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('EMPLOYEE_READ') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<List<EmployeeResponse>> getEmployees() {
        return BaseResponse.ok("Employees fetched successfully", employeeService.getEmployees());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('EMPLOYEE_READ') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<EmployeeResponse> getById(@PathVariable Long id) {
        return BaseResponse.ok("Employee fetched successfully", employeeService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EMPLOYEE_WRITE') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<EmployeeResponse> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        return BaseResponse.ok("Employee created successfully", employeeService.createEmployee(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EMPLOYEE_WRITE') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<EmployeeResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateEmployeeRequest request) {
        return BaseResponse.ok("Employee updated successfully", employeeService.update(id, request));
    }
}

