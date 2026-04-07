package com.yourcompany.salesmanagement.module.branch.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.branch.dto.request.CreateBranchRequest;
import com.yourcompany.salesmanagement.module.branch.dto.response.BranchResponse;
import com.yourcompany.salesmanagement.module.branch.entity.Branch;
import com.yourcompany.salesmanagement.module.branch.repository.BranchRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BranchServiceImpl implements com.yourcompany.salesmanagement.module.branch.service.BranchService {
    private final BranchRepository branchRepository;

    public BranchServiceImpl(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @Override
    public List<BranchResponse> getBranches() {
        Long storeId = SecurityUtils.requireStoreId();
        return branchRepository.findAllByStoreId(storeId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public BranchResponse createBranch(CreateBranchRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        Branch branch = new Branch();
        branch.setStoreId(storeId);
        branch.setName(request.name());
        branch.setCode(request.code());
        branch.setIsDefault(false);
        branch = branchRepository.save(branch);
        return toResponse(branch);
    }

    @Override
    @Transactional
    public BranchResponse setDefaultBranch(Long branchId) {
        Long storeId = SecurityUtils.requireStoreId();
        Branch branch = branchRepository.findByIdAndStoreId(branchId, storeId)
                .orElseThrow(() -> new BusinessException("Branch not found", HttpStatus.NOT_FOUND));

        branchRepository.clearDefaultByStoreId(storeId);
        branch.setIsDefault(true);
        branch = branchRepository.save(branch);
        return toResponse(branch);
    }

    private BranchResponse toResponse(Branch branch) {
        return new BranchResponse(branch.getId(), branch.getName(), branch.getCode(), Boolean.TRUE.equals(branch.getIsDefault()));
    }
}

