package com.yourcompany.salesmanagement.module.employee.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.branch.repository.BranchRepository;
import com.yourcompany.salesmanagement.module.employee.dto.request.CreateEmployeeRequest;
import com.yourcompany.salesmanagement.module.employee.dto.request.UpdateEmployeeRequest;
import com.yourcompany.salesmanagement.module.employee.dto.response.EmployeeResponse;
import com.yourcompany.salesmanagement.module.employee.entity.Employee;
import com.yourcompany.salesmanagement.module.employee.repository.EmployeeRepository;
import com.yourcompany.salesmanagement.module.employee.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final BranchRepository branchRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, BranchRepository branchRepository) {
        this.employeeRepository = employeeRepository;
        this.branchRepository = branchRepository;
    }

    @Override
    public List<EmployeeResponse> getEmployees() {
        Long storeId = SecurityUtils.requireStoreId();
        return employeeRepository.findAllByStoreId(storeId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public EmployeeResponse getById(Long id) {
        Long storeId = SecurityUtils.requireStoreId();
        Employee e = employeeRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new BusinessException("Employee not found", HttpStatus.NOT_FOUND));
        return toResponse(e);
    }

    @Override
    @Transactional
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        if (request.branchId() != null) {
            branchRepository.findByIdAndStoreId(request.branchId(), storeId)
                    .orElseThrow(() -> new BusinessException("Branch not found", HttpStatus.NOT_FOUND));
        }
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

    @Override
    @Transactional
    public EmployeeResponse update(Long id, UpdateEmployeeRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        Employee e = employeeRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new BusinessException("Employee not found", HttpStatus.NOT_FOUND));

        if (request.branchId() != null) {
            branchRepository.findByIdAndStoreId(request.branchId(), storeId)
                    .orElseThrow(() -> new BusinessException("Branch not found", HttpStatus.NOT_FOUND));
        }

        e.setBranchId(request.branchId());
        e.setUserId(request.userId());
        e.setEmployeeCode(request.employeeCode());
        e.setFullName(request.fullName());
        if (request.status() != null && !request.status().isBlank()) {
            e.setStatus(request.status().trim());
        }
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

