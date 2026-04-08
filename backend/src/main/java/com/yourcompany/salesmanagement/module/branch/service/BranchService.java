package com.yourcompany.salesmanagement.module.branch.service;

import com.yourcompany.salesmanagement.module.branch.dto.request.CreateBranchRequest;
import com.yourcompany.salesmanagement.module.branch.dto.response.BranchResponse;

import java.util.List;

public interface BranchService {
    List<BranchResponse> getBranches();

    BranchResponse createBranch(CreateBranchRequest request);

    BranchResponse setDefaultBranch(Long branchId);
}

