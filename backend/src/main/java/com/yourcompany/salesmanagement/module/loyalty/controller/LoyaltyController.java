package com.yourcompany.salesmanagement.module.loyalty.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.loyalty.dto.request.RedeemLoyaltyRequest;
import com.yourcompany.salesmanagement.module.loyalty.dto.response.LoyaltyAccountResponse;
import com.yourcompany.salesmanagement.module.loyalty.dto.response.LoyaltyRedeemResponse;
import com.yourcompany.salesmanagement.module.loyalty.dto.response.LoyaltyTransactionResponse;
import com.yourcompany.salesmanagement.module.loyalty.service.LoyaltyService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/loyalty", "/api/loyalty"})
public class LoyaltyController {
    private final LoyaltyService loyaltyService;

    public LoyaltyController(LoyaltyService loyaltyService) {
        this.loyaltyService = loyaltyService;
    }

    @GetMapping("/account")
    @PreAuthorize("hasAuthority('LOYALTY_READ') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<LoyaltyAccountResponse> account(@RequestParam Long customerId) {
        return BaseResponse.ok("OK", loyaltyService.getAccountByCustomerId(customerId));
    }

    @GetMapping("/transactions")
    @PreAuthorize("hasAuthority('LOYALTY_READ') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<List<LoyaltyTransactionResponse>> transactions(@RequestParam Long customerId) {
        return BaseResponse.ok("OK", loyaltyService.getTransactionsByCustomerId(customerId));
    }

    @PostMapping("/redeem")
    @PreAuthorize("hasAuthority('LOYALTY_REDEEM') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<LoyaltyRedeemResponse> redeem(@Valid @RequestBody RedeemLoyaltyRequest request) {
        return BaseResponse.ok("Redeemed successfully", loyaltyService.redeem(request));
    }
}

