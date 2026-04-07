package com.yourcompany.salesmanagement.module.employee.service;

import com.yourcompany.salesmanagement.module.employee.dto.request.CreateEmployeeRequest;
import com.yourcompany.salesmanagement.module.employee.dto.response.EmployeeResponse;

import java.util.List;

public interface EmployeeService {
    List<EmployeeResponse> getEmployees();

    EmployeeResponse createEmployee(CreateEmployeeRequest request);
}

