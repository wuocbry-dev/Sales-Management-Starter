package com.yourcompany.salesmanagement.module.branch.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.branch.dto.request.CreateBranchRequest;
import com.yourcompany.salesmanagement.module.branch.dto.response.BranchResponse;
import com.yourcompany.salesmanagement.module.branch.service.BranchService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/branches")
public class BranchController {
    private final BranchService branchService;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @GetMapping
    public BaseResponse<List<BranchResponse>> getBranches() {
        return BaseResponse.ok("Branches fetched successfully", branchService.getBranches());
    }

    @PostMapping
    public BaseResponse<BranchResponse> createBranch(@Valid @RequestBody CreateBranchRequest request) {
        return BaseResponse.ok("Branch created successfully", branchService.createBranch(request));
    }

    @PatchMapping("/{id}/default")
    public BaseResponse<BranchResponse> setDefault(@PathVariable Long id) {
        return BaseResponse.ok("Default branch updated successfully", branchService.setDefaultBranch(id));
    }
}

