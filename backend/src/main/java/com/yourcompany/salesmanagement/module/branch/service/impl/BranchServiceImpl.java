package com.yourcompany.salesmanagement.module.branch.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.branch.dto.request.CreateBranchRequest;
import com.yourcompany.salesmanagement.module.branch.dto.request.UpdateBranchRequest;
import com.yourcompany.salesmanagement.module.branch.dto.request.UpdateBranchStatusRequest;
import com.yourcompany.salesmanagement.module.branch.dto.response.BranchResponse;
import com.yourcompany.salesmanagement.module.branch.entity.Branch;
import com.yourcompany.salesmanagement.module.branch.repository.BranchRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class BranchServiceImpl implements com.yourcompany.salesmanagement.module.branch.service.BranchService {
    private final BranchRepository branchRepository;

    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_INACTIVE = "INACTIVE";

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
    public BranchResponse getById(Long id) {
        Long storeId = SecurityUtils.requireStoreId();
        Branch branch = branchRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new BusinessException("Branch not found", HttpStatus.NOT_FOUND));
        return toResponse(branch);
    }

    @Override
    public BranchResponse createBranch(CreateBranchRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        Branch branch = new Branch();
        branch.setStoreId(storeId);
        branch.setName(request.name());
        branch.setCode(request.code());
        branch.setIsDefault(false);
        branch.setStatus(STATUS_ACTIVE);
        branch = branchRepository.save(branch);
        return toResponse(branch);
    }

    @Override
    @Transactional
    public BranchResponse update(Long id, UpdateBranchRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        Branch branch = branchRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new BusinessException("Branch not found", HttpStatus.NOT_FOUND));
        branch.setName(request.name());
        branch.setCode(request.code());
        branch = branchRepository.save(branch);
        return toResponse(branch);
    }

    @Override
    @Transactional
    public BranchResponse setDefaultBranch(Long branchId) {
        Long storeId = SecurityUtils.requireStoreId();
        Branch branch = branchRepository.findByIdAndStoreId(branchId, storeId)
                .orElseThrow(() -> new BusinessException("Branch not found", HttpStatus.NOT_FOUND));

        if (STATUS_INACTIVE.equalsIgnoreCase(normalizeStatus(branch.getStatus()))) {
            throw new BusinessException("Cannot set an inactive branch as default", HttpStatus.BAD_REQUEST);
        }

        branchRepository.clearDefaultByStoreId(storeId);
        branch.setIsDefault(true);
        branch = branchRepository.save(branch);
        return toResponse(branch);
    }

    @Override
    @Transactional
    public BranchResponse updateStatus(Long id, UpdateBranchStatusRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        Branch branch = branchRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new BusinessException("Branch not found", HttpStatus.NOT_FOUND));

        String next = normalizeStatus(request.status());
        if (!STATUS_ACTIVE.equals(next) && !STATUS_INACTIVE.equals(next)) {
            throw new BusinessException("Invalid status. Allowed: ACTIVE, INACTIVE", HttpStatus.BAD_REQUEST);
        }

        if (STATUS_INACTIVE.equals(next) && Boolean.TRUE.equals(branch.getIsDefault())) {
            throw new BusinessException("Cannot inactivate the default branch. Set another default branch first.", HttpStatus.BAD_REQUEST);
        }

        branch.setStatus(next);
        branch = branchRepository.save(branch);
        return toResponse(branch);
    }

    private BranchResponse toResponse(Branch branch) {
        String status = normalizeStatus(branch.getStatus());
        return new BranchResponse(branch.getId(), branch.getName(), branch.getCode(), Boolean.TRUE.equals(branch.getIsDefault()), status);
    }

    private String normalizeStatus(String s) {
        if (s == null || s.isBlank()) return STATUS_ACTIVE;
        return s.trim().toUpperCase(Locale.ROOT);
    }
}

