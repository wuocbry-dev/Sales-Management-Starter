package com.yourcompany.salesmanagement.module.supplier.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateSupplierRequest(
        @NotBlank(message = "Name is required")
        String name,
        String contactName,
        String phone,
        String email,
        String address,
        String taxCode,
        String notes,
        String status
) {}

