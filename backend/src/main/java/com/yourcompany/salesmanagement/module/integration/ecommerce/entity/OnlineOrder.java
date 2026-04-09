package com.yourcompany.salesmanagement.module.integration.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "online_orders")
public class OnlineOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "channel_id", nullable = false)
    private Long channelId;

    @Column(name = "external_order_id", nullable = false, length = 100)
    private String externalOrderId;

    @Column(nullable = false, length = 30)
    private String status; // NEW/SYNCED/CONFIRMED/CANCELLED (foundation)

    @Column(name = "external_order_number", length = 100)
    private String externalOrderNumber;

    @Column(name = "buyer_name", length = 200)
    private String buyerName;

    @Column(name = "buyer_phone", length = 30)
    private String buyerPhone;

    @Column(name = "shipping_address", length = 255)
    private String shippingAddress;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "discount_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "shipping_fee", nullable = false, precision = 15, scale = 2)
    private BigDecimal shippingFee;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "items_json", columnDefinition = "json")
    private String itemsJson;

    @Column(name = "raw_payload", columnDefinition = "json")
    private String rawPayload;

    @Column(name = "synced_at")
    private LocalDateTime syncedAt;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}

