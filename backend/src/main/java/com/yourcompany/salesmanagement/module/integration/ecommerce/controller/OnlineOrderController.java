package com.yourcompany.salesmanagement.module.integration.ecommerce.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.integration.ecommerce.dto.response.OnlineOrderResponse;
import com.yourcompany.salesmanagement.module.integration.ecommerce.service.IntegrationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping({"/api/v1/online-orders", "/api/online-orders"})
public class OnlineOrderController {
    private final IntegrationService integrationService;

    public OnlineOrderController(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ONLINE_ORDER_READ') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<List<OnlineOrderResponse>> list(
            @RequestParam(required = false) Long channelId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to
    ) {
        return BaseResponse.ok("Online orders fetched successfully",
                integrationService.listOnlineOrders(channelId, status, from, to));
    }
}

