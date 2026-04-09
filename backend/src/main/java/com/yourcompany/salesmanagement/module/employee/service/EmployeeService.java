package com.yourcompany.salesmanagement.module.employee.service;

import com.yourcompany.salesmanagement.module.employee.dto.request.CreateEmployeeRequest;
import com.yourcompany.salesmanagement.module.employee.dto.request.UpdateEmployeeRequest;
import com.yourcompany.salesmanagement.module.employee.dto.response.EmployeeResponse;

import java.util.List;

public interface EmployeeService {
    List<EmployeeResponse> getEmployees();

    EmployeeResponse getById(Long id);

    EmployeeResponse createEmployee(CreateEmployeeRequest request);

    EmployeeResponse update(Long id, UpdateEmployeeRequest request);
}

