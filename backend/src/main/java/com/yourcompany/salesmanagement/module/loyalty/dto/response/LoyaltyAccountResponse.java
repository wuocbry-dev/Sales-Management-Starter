package com.yourcompany.salesmanagement.module.loyalty.dto.response;

public record LoyaltyAccountResponse(
        Long id,
        Long customerId,
        int currentPoints,
        int lifetimePoints,
        String tierName
) {}

