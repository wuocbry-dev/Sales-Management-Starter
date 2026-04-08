package com.yourcompany.salesmanagement.module.integration.ecommerce.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.integration.ecommerce.dto.request.CreateIntegrationChannelRequest;
import com.yourcompany.salesmanagement.module.integration.ecommerce.dto.request.CreateIntegrationProductMappingRequest;
import com.yourcompany.salesmanagement.module.integration.ecommerce.dto.response.IntegrationChannelResponse;
import com.yourcompany.salesmanagement.module.integration.ecommerce.dto.response.IntegrationProductMappingResponse;
import com.yourcompany.salesmanagement.module.integration.ecommerce.entity.IntegrationChannel;
import com.yourcompany.salesmanagement.module.integration.ecommerce.entity.IntegrationProductMapping;
import com.yourcompany.salesmanagement.module.integration.ecommerce.repository.IntegrationChannelRepository;
import com.yourcompany.salesmanagement.module.integration.ecommerce.repository.IntegrationProductMappingRepository;
import com.yourcompany.salesmanagement.module.integration.ecommerce.service.IntegrationService;
import com.yourcompany.salesmanagement.module.product.repository.ProductRepository;
import com.yourcompany.salesmanagement.module.variant.repository.ProductVariantRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IntegrationServiceImpl implements IntegrationService {
    private final IntegrationChannelRepository channelRepository;
    private final IntegrationProductMappingRepository mappingRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ObjectMapper objectMapper;

    public IntegrationServiceImpl(
            IntegrationChannelRepository channelRepository,
            IntegrationProductMappingRepository mappingRepository,
            ProductRepository productRepository,
            ProductVariantRepository productVariantRepository,
            ObjectMapper objectMapper) {
        this.channelRepository = channelRepository;
        this.mappingRepository = mappingRepository;
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<IntegrationChannelResponse> listChannels() {
        Long storeId = SecurityUtils.requireStoreId();
        return channelRepository.findAllByStoreIdOrderByIdDesc(storeId).stream()
                .map(this::toChannelResponse)
                .toList();
    }

    @Override
    @Transactional
    public IntegrationChannelResponse createChannel(CreateIntegrationChannelRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        String channelCode = request.channelCode().trim().toUpperCase();
        if (channelRepository.existsByStoreIdAndChannelCode(storeId, channelCode)) {
            throw new BusinessException("Channel code already exists", HttpStatus.CONFLICT);
        }

        IntegrationChannel c = new IntegrationChannel();
        c.setStoreId(storeId);
        c.setChannelType(request.channelType().trim().toUpperCase());
        c.setChannelName(request.channelName().trim());
        c.setChannelCode(channelCode);
        c.setStatus(request.status() == null || request.status().isBlank() ? "ACTIVE" : request.status().trim());
        c.setConfigJson(normalizeConfigJson(request.configJson()));
        c = channelRepository.save(c);
        return toChannelResponse(c);
    }

    @Override
    public List<IntegrationProductMappingResponse> listProductMappings() {
        Long storeId = SecurityUtils.requireStoreId();
        return mappingRepository.findAllByStoreIdOrderByIdDesc(storeId).stream()
                .map(this::toMappingResponse)
                .toList();
    }

    @Override
    @Transactional
    public IntegrationProductMappingResponse createProductMapping(CreateIntegrationProductMappingRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        IntegrationChannel channel = channelRepository.findByIdAndStoreId(request.channelId(), storeId)
                .orElseThrow(() -> new BusinessException("Channel not found", HttpStatus.NOT_FOUND));

        productRepository.findByIdAndStoreId(request.productId(), storeId)
                .orElseThrow(() -> new BusinessException("Product not found", HttpStatus.NOT_FOUND));

        if (request.variantId() != null) {
            productVariantRepository.findByIdAndProductId(request.variantId(), request.productId())
                    .orElseThrow(() -> new BusinessException("Variant not found for this product", HttpStatus.NOT_FOUND));
        }

        boolean exists = request.variantId() == null
                ? mappingRepository.existsByStoreIdAndChannelIdAndProductIdAndVariantIdIsNull(storeId, channel.getId(), request.productId())
                : mappingRepository.existsByStoreIdAndChannelIdAndProductIdAndVariantId(storeId, channel.getId(), request.productId(), request.variantId());
        if (exists) {
            throw new BusinessException("Product mapping already exists for this channel/product/variant", HttpStatus.CONFLICT);
        }

        IntegrationProductMapping m = new IntegrationProductMapping();
        m.setStoreId(storeId);
        m.setChannelId(channel.getId());
        m.setProductId(request.productId());
        m.setVariantId(request.variantId());
        m.setExternalProductId(request.externalProductId());
        m.setExternalVariantId(request.externalVariantId());
        m.setExternalSku(request.externalSku());
        m.setStatus(request.status() == null || request.status().isBlank() ? "ACTIVE" : request.status().trim());
        m = mappingRepository.save(m);
        return toMappingResponse(m);
    }

    private String normalizeConfigJson(String configJson) {
        if (configJson == null || configJson.isBlank()) return null;
        try {
            var node = objectMapper.readTree(configJson);
            return objectMapper.writeValueAsString(node);
        } catch (Exception e) {
            throw new BusinessException("configJson must be valid JSON", HttpStatus.BAD_REQUEST);
        }
    }

    private IntegrationChannelResponse toChannelResponse(IntegrationChannel c) {
        return new IntegrationChannelResponse(
                c.getId(),
                c.getChannelType(),
                c.getChannelName(),
                c.getChannelCode(),
                c.getStatus(),
                c.getConfigJson()
        );
    }

    private IntegrationProductMappingResponse toMappingResponse(IntegrationProductMapping m) {
        return new IntegrationProductMappingResponse(
                m.getId(),
                m.getChannelId(),
                m.getProductId(),
                m.getVariantId(),
                m.getExternalProductId(),
                m.getExternalVariantId(),
                m.getExternalSku(),
                m.getStatus()
        );
    }
}

