package com.yourcompany.salesmanagement.module.branch.service;

import com.yourcompany.salesmanagement.module.branch.dto.request.CreateBranchRequest;
import com.yourcompany.salesmanagement.module.branch.dto.request.UpdateBranchRequest;
import com.yourcompany.salesmanagement.module.branch.dto.request.UpdateBranchStatusRequest;
import com.yourcompany.salesmanagement.module.branch.dto.response.BranchResponse;

import java.util.List;

public interface BranchService {
    List<BranchResponse> getBranches();

    BranchResponse getById(Long id);

    BranchResponse createBranch(CreateBranchRequest request);

    BranchResponse update(Long id, UpdateBranchRequest request);

    BranchResponse setDefaultBranch(Long branchId);

    BranchResponse updateStatus(Long id, UpdateBranchStatusRequest request);
}

