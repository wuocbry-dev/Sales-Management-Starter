package com.yourcompany.salesmanagement.module.loyalty.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.loyalty.dto.response.LoyaltyAccountResponse;
import com.yourcompany.salesmanagement.module.loyalty.dto.response.LoyaltyTransactionResponse;
import com.yourcompany.salesmanagement.module.loyalty.service.LoyaltyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loyalty")
public class LoyaltyController {
    private final LoyaltyService loyaltyService;

    public LoyaltyController(LoyaltyService loyaltyService) {
        this.loyaltyService = loyaltyService;
    }

    @GetMapping("/account")
    public BaseResponse<LoyaltyAccountResponse> account(@RequestParam Long customerId) {
        return BaseResponse.ok("OK", loyaltyService.getAccountByCustomerId(customerId));
    }

    @GetMapping("/transactions")
    public BaseResponse<List<LoyaltyTransactionResponse>> transactions(@RequestParam Long customerId) {
        return BaseResponse.ok("OK", loyaltyService.getTransactionsByCustomerId(customerId));
    }
}

