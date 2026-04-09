package com.yourcompany.salesmanagement.module.loyalty.dto.response;

public record LoyaltyRedeemResponse(
        LoyaltyAccountResponse account,
        LoyaltyTransactionResponse transaction
) {}

