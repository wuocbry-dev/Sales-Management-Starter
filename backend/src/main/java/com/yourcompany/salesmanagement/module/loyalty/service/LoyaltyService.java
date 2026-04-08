package com.yourcompany.salesmanagement.module.loyalty.service;

import com.yourcompany.salesmanagement.module.loyalty.dto.response.LoyaltyAccountResponse;
import com.yourcompany.salesmanagement.module.loyalty.dto.response.LoyaltyTransactionResponse;

import java.util.List;

public interface LoyaltyService {
    LoyaltyAccountResponse getAccountByCustomerId(Long customerId);

    List<LoyaltyTransactionResponse> getTransactionsByCustomerId(Long customerId);

    void earnPointsForSalesOrder(Long customerId, Long salesOrderId, int points, String description);
}

