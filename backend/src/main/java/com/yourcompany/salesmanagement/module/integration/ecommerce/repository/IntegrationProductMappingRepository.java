package com.yourcompany.salesmanagement.module.integration.ecommerce.repository;

import com.yourcompany.salesmanagement.module.integration.ecommerce.entity.IntegrationProductMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IntegrationProductMappingRepository extends JpaRepository<IntegrationProductMapping, Long> {
    List<IntegrationProductMapping> findAllByStoreIdOrderByIdDesc(Long storeId);

    boolean existsByStoreIdAndChannelIdAndProductIdAndVariantIdIsNull(Long storeId, Long channelId, Long productId);

    boolean existsByStoreIdAndChannelIdAndProductIdAndVariantId(Long storeId, Long channelId, Long productId, Long variantId);
}

