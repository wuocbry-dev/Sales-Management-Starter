package com.yourcompany.salesmanagement.module.product.dto.response;

public record ProductResponse(
        Long id,
        String code,
        String name,
        String category,
        Double price,
        Integer stock,
        String status
) {}
