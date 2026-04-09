package com.yourcompany.salesmanagement.module.integration.ecommerce.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.integration.ecommerce.dto.request.CreateIntegrationChannelRequest;
import com.yourcompany.salesmanagement.module.integration.ecommerce.dto.request.CreateIntegrationProductMappingRequest;
import com.yourcompany.salesmanagement.module.integration.ecommerce.dto.response.IntegrationChannelResponse;
import com.yourcompany.salesmanagement.module.integration.ecommerce.dto.response.IntegrationProductMappingResponse;
import com.yourcompany.salesmanagement.module.integration.ecommerce.dto.response.IntegrationSyncResponse;
import com.yourcompany.salesmanagement.module.integration.ecommerce.service.IntegrationService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/integrations", "/api/integrations"})
public class IntegrationController {
    private final IntegrationService integrationService;

    public IntegrationController(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @GetMapping("/channels")
    @PreAuthorize("hasAuthority('INTEGRATION_READ') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<List<IntegrationChannelResponse>> listChannels() {
        return BaseResponse.ok("Integration channels fetched successfully", integrationService.listChannels());
    }

    @PostMapping("/channels")
    @PreAuthorize("hasAuthority('INTEGRATION_WRITE') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<IntegrationChannelResponse> createChannel(@Valid @RequestBody CreateIntegrationChannelRequest request) {
        return BaseResponse.ok("Integration channel created successfully", integrationService.createChannel(request));
    }

    @GetMapping("/product-mappings")
    @PreAuthorize("hasAuthority('INTEGRATION_READ') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<List<IntegrationProductMappingResponse>> listProductMappings() {
        return BaseResponse.ok("Integration product mappings fetched successfully", integrationService.listProductMappings());
    }

    @PostMapping("/product-mappings")
    @PreAuthorize("hasAuthority('INTEGRATION_WRITE') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<IntegrationProductMappingResponse> createProductMapping(@Valid @RequestBody CreateIntegrationProductMappingRequest request) {
        return BaseResponse.ok("Integration product mapping created successfully", integrationService.createProductMapping(request));
    }

    @PostMapping("/{channelId}/sync-orders")
    @PreAuthorize("hasAuthority('INTEGRATION_SYNC_ORDERS') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<IntegrationSyncResponse> syncOrders(@PathVariable Long channelId) {
        return BaseResponse.ok("Sync started", integrationService.syncOrders(channelId));
    }
}

