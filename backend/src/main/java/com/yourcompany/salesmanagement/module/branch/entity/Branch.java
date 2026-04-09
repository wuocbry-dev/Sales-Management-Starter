package com.yourcompany.salesmanagement.module.branch.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "branches")
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;

    /**
     * Branch operational status.
     * Values: ACTIVE, INACTIVE
     *
     * Note: database migration required to add column {@code status}.
     */
    @Column(nullable = false, length = 20)
    private String status;

    @PrePersist
    public void prePersist() {
        if (status == null || status.isBlank()) status = "ACTIVE";
        if (isDefault == null) isDefault = false;
    }
}

