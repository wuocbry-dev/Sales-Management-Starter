package com.yourcompany.salesmanagement.module.loyalty.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "loyalty_accounts")
public class LoyaltyAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "current_points", nullable = false)
    private Integer currentPoints;

    @Column(name = "lifetime_points", nullable = false)
    private Integer lifetimePoints;

    @Column(name = "tier_name", nullable = false, length = 50)
    private String tierName;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}

