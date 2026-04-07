package com.yourcompany.salesmanagement.module.permission.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "permissions")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100, unique = true)
    private String code;

    @Column(name = "module_name", nullable = false, length = 50)
    private String moduleName;

    @Column(length = 255)
    private String description;
}

