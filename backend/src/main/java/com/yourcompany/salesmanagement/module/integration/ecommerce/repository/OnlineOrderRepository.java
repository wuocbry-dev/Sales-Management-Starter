package com.yourcompany.salesmanagement.module.integration.ecommerce.repository;

import com.yourcompany.salesmanagement.module.integration.ecommerce.entity.OnlineOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OnlineOrderRepository extends JpaRepository<OnlineOrder, Long> {
    Optional<OnlineOrder> findByIdAndStoreId(Long id, Long storeId);

    boolean existsByStoreIdAndChannelIdAndExternalOrderId(Long storeId, Long channelId, String externalOrderId);

    @Query("""
            select o
            from OnlineOrder o
            where o.storeId = :storeId
              and (:channelId is null or o.channelId = :channelId)
              and (:status is null or lower(o.status) = lower(:status))
              and (:fromTs is null or o.createdAt >= :fromTs)
              and (:toTs is null or o.createdAt <= :toTs)
            order by o.createdAt desc, o.id desc
            """)
    List<OnlineOrder> search(@Param("storeId") Long storeId,
                             @Param("channelId") Long channelId,
                             @Param("status") String status,
                             @Param("fromTs") LocalDateTime fromTs,
                             @Param("toTs") LocalDateTime toTs);
}

