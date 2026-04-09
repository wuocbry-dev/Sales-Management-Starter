package com.yourcompany.salesmanagement.module.integration.ecommerce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "integration_channels")
public class IntegrationChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "channel_type", nullable = false, length = 30)
    private String channelType;

    @Column(name = "channel_name", nullable = false, length = 150)
    private String channelName;

    @Column(name = "channel_code", nullable = false, length = 50)
    private String channelCode;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "config_json", columnDefinition = "json")
    private String configJson;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}

