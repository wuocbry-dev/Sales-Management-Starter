package com.yourcompany.salesmanagement.module.branch.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateBranchStatusRequest(
        @NotBlank(message = "Status is required")
        String status
) {}

