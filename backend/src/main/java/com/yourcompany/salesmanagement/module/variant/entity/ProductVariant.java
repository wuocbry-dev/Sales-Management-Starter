package com.yourcompany.salesmanagement.module.variant.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "product_variants")
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false, length = 100)
    private String sku;

    @Column(length = 100)
    private String barcode;

    @Column(name = "variant_name", nullable = false, length = 150)
    private String variantName;

    @Column(name = "option1_name", length = 50)
    private String option1Name;

    @Column(name = "option1_value", length = 100)
    private String option1Value;

    @Column(name = "option2_name", length = 50)
    private String option2Name;

    @Column(name = "option2_value", length = 100)
    private String option2Value;

    @Column(name = "cost_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal costPrice;

    @Column(name = "selling_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal sellingPrice;

    @Column(nullable = false, length = 20)
    private String status;
}

