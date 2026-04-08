package com.yourcompany.salesmanagement.module.supplier.dto.response;

public record SupplierResponse(
        Long id,
        String name,
        String contactName,
        String phone,
        String email,
        String status
) {}

