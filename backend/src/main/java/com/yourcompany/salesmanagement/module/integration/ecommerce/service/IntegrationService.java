package com.yourcompany.salesmanagement.module.integration.ecommerce.service;

import com.yourcompany.salesmanagement.module.integration.ecommerce.dto.request.CreateIntegrationChannelRequest;
import com.yourcompany.salesmanagement.module.integration.ecommerce.dto.request.CreateIntegrationProductMappingRequest;
import com.yourcompany.salesmanagement.module.integration.ecommerce.dto.response.IntegrationChannelResponse;
import com.yourcompany.salesmanagement.module.integration.ecommerce.dto.response.IntegrationProductMappingResponse;

import java.util.List;

public interface IntegrationService {
    List<IntegrationChannelResponse> listChannels();

    IntegrationChannelResponse createChannel(CreateIntegrationChannelRequest request);

    List<IntegrationProductMappingResponse> listProductMappings();

    IntegrationProductMappingResponse createProductMapping(CreateIntegrationProductMappingRequest request);
}

