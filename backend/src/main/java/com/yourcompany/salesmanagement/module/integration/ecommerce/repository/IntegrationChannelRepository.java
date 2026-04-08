package com.yourcompany.salesmanagement.module.integration.ecommerce.repository;

import com.yourcompany.salesmanagement.module.integration.ecommerce.entity.IntegrationChannel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IntegrationChannelRepository extends JpaRepository<IntegrationChannel, Long> {
    List<IntegrationChannel> findAllByStoreIdOrderByIdDesc(Long storeId);

    Optional<IntegrationChannel> findByIdAndStoreId(Long id, Long storeId);

    boolean existsByStoreIdAndChannelCode(Long storeId, String channelCode);
}

