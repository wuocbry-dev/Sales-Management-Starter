package com.yourcompany.salesmanagement.module.employee.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.employee.dto.request.CreateEmployeeRequest;
import com.yourcompany.salesmanagement.module.employee.dto.response.EmployeeResponse;
import com.yourcompany.salesmanagement.module.employee.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public BaseResponse<List<EmployeeResponse>> getEmployees() {
        return BaseResponse.ok("Employees fetched successfully", employeeService.getEmployees());
    }

    @PostMapping
    public BaseResponse<EmployeeResponse> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        return BaseResponse.ok("Employee created successfully", employeeService.createEmployee(request));
    }
}

