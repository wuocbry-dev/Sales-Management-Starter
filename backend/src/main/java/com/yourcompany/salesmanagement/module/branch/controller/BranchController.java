package com.yourcompany.salesmanagement.module.branch.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.branch.dto.request.CreateBranchRequest;
import com.yourcompany.salesmanagement.module.branch.dto.request.UpdateBranchRequest;
import com.yourcompany.salesmanagement.module.branch.dto.request.UpdateBranchStatusRequest;
import com.yourcompany.salesmanagement.module.branch.dto.response.BranchResponse;
import com.yourcompany.salesmanagement.module.branch.service.BranchService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/branches", "/api/branches"})
public class BranchController {
    private final BranchService branchService;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('BRANCH_READ') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<List<BranchResponse>> getBranches() {
        return BaseResponse.ok("Branches fetched successfully", branchService.getBranches());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('BRANCH_READ') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<BranchResponse> getById(@PathVariable Long id) {
        return BaseResponse.ok("Branch fetched successfully", branchService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('BRANCH_WRITE') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<BranchResponse> createBranch(@Valid @RequestBody CreateBranchRequest request) {
        return BaseResponse.ok("Branch created successfully", branchService.createBranch(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('BRANCH_WRITE') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<BranchResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateBranchRequest request) {
        return BaseResponse.ok("Branch updated successfully", branchService.update(id, request));
    }

    @PatchMapping("/{id}/default")
    @PreAuthorize("hasAuthority('BRANCH_WRITE') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<BranchResponse> setDefault(@PathVariable Long id) {
        return BaseResponse.ok("Default branch updated successfully", branchService.setDefaultBranch(id));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('BRANCH_WRITE') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<BranchResponse> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateBranchStatusRequest request) {
        return BaseResponse.ok("Branch status updated successfully", branchService.updateStatus(id, request));
    }
}

