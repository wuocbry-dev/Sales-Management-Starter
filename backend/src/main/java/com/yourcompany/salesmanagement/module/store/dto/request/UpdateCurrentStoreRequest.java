package com.yourcompany.salesmanagement.module.store.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateCurrentStoreRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Business type is required")
        String businessType
) {}

