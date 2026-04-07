package com.yourcompany.salesmanagement.module.employee.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.module.employee.dto.request.CreateEmployeeRequest;
import com.yourcompany.salesmanagement.module.employee.dto.response.EmployeeResponse;
import com.yourcompany.salesmanagement.module.employee.entity.Employee;
import com.yourcompany.salesmanagement.module.employee.repository.EmployeeRepository;
import com.yourcompany.salesmanagement.module.employee.service.EmployeeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<EmployeeResponse> getEmployees() {
        Long storeId = SecurityUtils.requireStoreId();
        return employeeRepository.findAllByStoreId(storeId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        Employee e = new Employee();
        e.setStoreId(storeId);
        e.setBranchId(request.branchId());
        e.setUserId(request.userId());
        e.setEmployeeCode(request.employeeCode());
        e.setFullName(request.fullName());
        e.setStatus("ACTIVE");
        e = employeeRepository.save(e);
        return toResponse(e);
    }

    private EmployeeResponse toResponse(Employee e) {
        return new EmployeeResponse(
                e.getId(),
                e.getBranchId(),
                e.getUserId(),
                e.getEmployeeCode(),
                e.getFullName(),
                e.getStatus()
        );
    }
}

