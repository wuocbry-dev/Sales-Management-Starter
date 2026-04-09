package com.yourcompany.salesmanagement.module.integration.ecommerce.service;

import com.yourcompany.salesmanagement.module.integration.ecommerce.dto.request.CreateIntegrationChannelRequest;
import com.yourcompany.salesmanagement.module.integration.ecommerce.dto.request.CreateIntegrationProductMappingRequest;
import com.yourcompany.salesmanagement.module.integration.ecommerce.dto.response.IntegrationChannelResponse;
import com.yourcompany.salesmanagement.module.integration.ecommerce.dto.response.IntegrationProductMappingResponse;
import com.yourcompany.salesmanagement.module.integration.ecommerce.dto.response.IntegrationSyncResponse;
import com.yourcompany.salesmanagement.module.integration.ecommerce.dto.response.OnlineOrderResponse;

import java.util.List;
import java.time.LocalDateTime;

public interface IntegrationService {
    List<IntegrationChannelResponse> listChannels();

    IntegrationChannelResponse createChannel(CreateIntegrationChannelRequest request);

    List<IntegrationProductMappingResponse> listProductMappings();

    IntegrationProductMappingResponse createProductMapping(CreateIntegrationProductMappingRequest request);

    IntegrationSyncResponse syncOrders(Long channelId);

    List<OnlineOrderResponse> listOnlineOrders(Long channelId, String status, LocalDateTime from, LocalDateTime to);
}

