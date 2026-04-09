package com.yourcompany.salesmanagement.module.integration.ecommerce.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.integration.ecommerce.dto.request.CreateIntegrationChannelRequest;
import com.yourcompany.salesmanagement.module.integration.ecommerce.dto.request.CreateIntegrationProductMappingRequest;
import com.yourcompany.salesmanagement.module.integration.ecommerce.dto.response.IntegrationChannelResponse;
import com.yourcompany.salesmanagement.module.integration.ecommerce.dto.response.IntegrationProductMappingResponse;
import com.yourcompany.salesmanagement.module.integration.ecommerce.dto.response.IntegrationSyncResponse;
import com.yourcompany.salesmanagement.module.integration.ecommerce.dto.response.OnlineOrderResponse;
import com.yourcompany.salesmanagement.module.integration.ecommerce.entity.IntegrationChannel;
import com.yourcompany.salesmanagement.module.integration.ecommerce.entity.IntegrationProductMapping;
import com.yourcompany.salesmanagement.module.integration.ecommerce.entity.OnlineOrder;
import com.yourcompany.salesmanagement.module.integration.ecommerce.repository.IntegrationChannelRepository;
import com.yourcompany.salesmanagement.module.integration.ecommerce.repository.IntegrationProductMappingRepository;
import com.yourcompany.salesmanagement.module.integration.ecommerce.repository.OnlineOrderRepository;
import com.yourcompany.salesmanagement.module.integration.ecommerce.service.IntegrationService;
import com.yourcompany.salesmanagement.module.product.repository.ProductRepository;
import com.yourcompany.salesmanagement.module.variant.repository.ProductVariantRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class IntegrationServiceImpl implements IntegrationService {
    private final IntegrationChannelRepository channelRepository;
    private final IntegrationProductMappingRepository mappingRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ObjectMapper objectMapper;
    private final OnlineOrderRepository onlineOrderRepository;

    public IntegrationServiceImpl(
            IntegrationChannelRepository channelRepository,
            IntegrationProductMappingRepository mappingRepository,
            ProductRepository productRepository,
            ProductVariantRepository productVariantRepository,
            ObjectMapper objectMapper,
            OnlineOrderRepository onlineOrderRepository) {
        this.channelRepository = channelRepository;
        this.mappingRepository = mappingRepository;
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.objectMapper = objectMapper;
        this.onlineOrderRepository = onlineOrderRepository;
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

    @Override
    @Transactional
    public IntegrationSyncResponse syncOrders(Long channelId) {
        Long storeId = SecurityUtils.requireStoreId();
        IntegrationChannel channel = channelRepository.findByIdAndStoreId(channelId, storeId)
                .orElseThrow(() -> new BusinessException("Channel not found", HttpStatus.NOT_FOUND));

        if (!"ACTIVE".equalsIgnoreCase(channel.getStatus())) {
            throw new BusinessException("Channel is not ACTIVE", HttpStatus.BAD_REQUEST);
        }

        // Foundation: trigger async mock sync to create a few online orders
        mockSyncOrdersAsync(storeId, channel.getId(), channel.getChannelCode());
        return new IntegrationSyncResponse(channel.getId(), "STARTED", "Mock sync started");
    }

    @Override
    public List<OnlineOrderResponse> listOnlineOrders(Long channelId, String status, LocalDateTime from, LocalDateTime to) {
        Long storeId = SecurityUtils.requireStoreId();
        return onlineOrderRepository.search(storeId, channelId, status, from, to).stream()
                .map(this::toOnlineOrderResponse)
                .toList();
    }

    @Async
    @Transactional
    protected void mockSyncOrdersAsync(Long storeId, Long channelId, String channelCode) {
        // Create 3 mock orders per sync call (foundation)
        for (int i = 0; i < 3; i++) {
            String extId = channelCode + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            if (onlineOrderRepository.existsByStoreIdAndChannelIdAndExternalOrderId(storeId, channelId, extId)) continue;

            BigDecimal subtotal = money(new BigDecimal("100000").add(new BigDecimal(String.valueOf(i * 5000))));
            BigDecimal discount = money(BigDecimal.ZERO);
            BigDecimal shipping = money(new BigDecimal("15000"));
            BigDecimal total = money(subtotal.subtract(discount).add(shipping));

            OnlineOrder o = new OnlineOrder();
            o.setStoreId(storeId);
            o.setChannelId(channelId);
            o.setExternalOrderId(extId);
            o.setExternalOrderNumber("ONL-" + extId.substring(extId.length() - 6));
            o.setStatus("SYNCED");
            o.setBuyerName("Online Buyer " + (i + 1));
            o.setBuyerPhone("09000000" + i);
            o.setShippingAddress("Mock address");
            o.setSubtotal(subtotal);
            o.setDiscountAmount(discount);
            o.setShippingFee(shipping);
            o.setTotalAmount(total);
            o.setItemsJson(writeJson(Map.of("items", List.of(
                    Map.of("sku", "SKU-DEMO", "name", "Demo product", "qty", 1, "price", subtotal)
            ))));
            o.setRawPayload(writeJson(Map.of("provider", "MOCK", "channelId", channelId, "externalOrderId", extId)));
            o.setSyncedAt(LocalDateTime.now());
            onlineOrderRepository.save(o);
        }
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

    private String writeJson(Object v) {
        try {
            return objectMapper.writeValueAsString(v);
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal money(BigDecimal v) {
        if (v == null) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return v.setScale(2, RoundingMode.HALF_UP);
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

    private OnlineOrderResponse toOnlineOrderResponse(OnlineOrder o) {
        return new OnlineOrderResponse(
                o.getId(),
                o.getChannelId(),
                o.getExternalOrderId(),
                o.getExternalOrderNumber(),
                o.getStatus(),
                o.getBuyerName(),
                o.getBuyerPhone(),
                o.getTotalAmount(),
                o.getSyncedAt(),
                o.getCreatedAt()
        );
    }
}

