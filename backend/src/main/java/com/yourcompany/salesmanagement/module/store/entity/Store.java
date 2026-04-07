package com.yourcompany.salesmanagement.module.store.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "stores")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 50, unique = true)
    private String code;

    @Column(name = "business_type", nullable = false, length = 50)
    private String businessType;

    @Column(name = "owner_user_id")
    private Long ownerUserId;
}

