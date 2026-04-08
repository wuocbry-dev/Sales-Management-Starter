package com.yourcompany.salesmanagement.module.inventory.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "inventories")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "branch_id", nullable = false)
    private Long branchId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "variant_id")
    private Long variantId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal quantity;

    @Column(name = "reserved_quantity", nullable = false, precision = 15, scale = 2)
    private BigDecimal reservedQuantity;

    @Column(name = "min_quantity", nullable = false, precision = 15, scale = 2)
    private BigDecimal minQuantity;

    @Column(name = "max_quantity", precision = 15, scale = 2)
    private BigDecimal maxQuantity;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

