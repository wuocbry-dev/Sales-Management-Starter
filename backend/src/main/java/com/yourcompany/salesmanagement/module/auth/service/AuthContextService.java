package com.yourcompany.salesmanagement.module.auth.service;

import com.yourcompany.salesmanagement.module.auth.service.dto.AuthContext;
import com.yourcompany.salesmanagement.module.branch.repository.BranchRepository;
import com.yourcompany.salesmanagement.module.employee.repository.EmployeeRepository;
import com.yourcompany.salesmanagement.module.store.repository.StoreRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthContextService {
    private final EmployeeRepository employeeRepository;
    private final StoreRepository storeRepository;
    private final BranchRepository branchRepository;

    public AuthContextService(EmployeeRepository employeeRepository, StoreRepository storeRepository, BranchRepository branchRepository) {
        this.employeeRepository = employeeRepository;
        this.storeRepository = storeRepository;
        this.branchRepository = branchRepository;
    }

    public AuthContext resolveForUserId(Long userId) {
        var employeeOpt = employeeRepository.findFirstByUserId(userId);
        if (employeeOpt.isPresent()) {
            var e = employeeOpt.get();
            Long storeId = e.getStoreId();
            Long branchId = e.getBranchId();
            if (branchId == null && storeId != null) {
                branchId = branchRepository.findFirstByStoreIdAndIsDefaultTrue(storeId).map(b -> b.getId()).orElse(null);
            }
            return new AuthContext(storeId, branchId);
        }

        Long storeId = storeRepository.findFirstByOwnerUserId(userId).map(s -> s.getId()).orElse(null);
        Long branchId = storeId == null ? null : branchRepository.findFirstByStoreIdAndIsDefaultTrue(storeId).map(b -> b.getId()).orElse(null);
        return new AuthContext(storeId, branchId);
    }
}

