-- ============================================================
-- Sales Management Starter - Database (ALL-IN-ONE)
-- Generated: 2026-04-09
-- MySQL 8.0+ schema for Sales Management Starter
-- Charset: utf8mb4 / Collation: utf8mb4_unicode_ci

CREATE DATABASE IF NOT EXISTS sales_management
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE sales_management;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS return_order_items;
DROP TABLE IF EXISTS return_orders;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS settings;
DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS loyalty_transactions;
DROP TABLE IF EXISTS loyalty_accounts;
DROP TABLE IF EXISTS integration_product_mappings;
DROP TABLE IF EXISTS integration_channels;
DROP TABLE IF EXISTS vouchers;
DROP TABLE IF EXISTS promotions;
DROP TABLE IF EXISTS cashbook_entries;
DROP TABLE IF EXISTS shifts;
DROP TABLE IF EXISTS shipments;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS sales_order_items;
DROP TABLE IF EXISTS sales_orders;
DROP TABLE IF EXISTS purchase_order_items;
DROP TABLE IF EXISTS purchase_orders;
DROP TABLE IF EXISTS inventories;
DROP TABLE IF EXISTS inventory_movements;
DROP TABLE IF EXISTS product_variants;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS suppliers;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS branches;
DROP TABLE IF EXISTS stores;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS role_permissions;
DROP TABLE IF EXISTS permissions;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS users;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE users (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(150) NULL,
    phone VARCHAR(20) NULL,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    last_login_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_phone UNIQUE (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE roles (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL,
    description VARCHAR(255) NULL,
    is_system TINYINT(1) NOT NULL DEFAULT 1,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_roles_code UNIQUE (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE permissions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(100) NOT NULL,
    module_name VARCHAR(50) NOT NULL,
    description VARCHAR(255) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_permissions_code UNIQUE (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE role_permissions (
    role_id BIGINT UNSIGNED NOT NULL,
    permission_id BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE user_roles (
    user_id BIGINT UNSIGNED NOT NULL,
    role_id BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE stores (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    code VARCHAR(50) NOT NULL,
    business_type VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NULL,
    email VARCHAR(150) NULL,
    address VARCHAR(255) NULL,
    tax_code VARCHAR(50) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    owner_user_id BIGINT UNSIGNED NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_stores_code UNIQUE (code),
    CONSTRAINT fk_stores_owner FOREIGN KEY (owner_user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE branches (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT UNSIGNED NOT NULL,
    name VARCHAR(150) NOT NULL,
    code VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NULL,
    email VARCHAR(150) NULL,
    address VARCHAR(255) NULL,
    is_default TINYINT(1) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_branches_store_code UNIQUE (store_id, code),
    CONSTRAINT fk_branches_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE employees (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT UNSIGNED NOT NULL,
    branch_id BIGINT UNSIGNED NULL,
    user_id BIGINT UNSIGNED NULL,
    employee_code VARCHAR(50) NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    phone VARCHAR(20) NULL,
    email VARCHAR(150) NULL,
    position_name VARCHAR(100) NULL,
    hire_date DATE NULL,
    salary DECIMAL(15,2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_employees_store_code UNIQUE (store_id, employee_code),
    CONSTRAINT fk_employees_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    CONSTRAINT fk_employees_branch FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE SET NULL,
    CONSTRAINT fk_employees_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE categories (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT UNSIGNED NOT NULL,
    parent_id BIGINT UNSIGNED NULL,
    name VARCHAR(150) NOT NULL,
    slug VARCHAR(180) NULL,
    description VARCHAR(255) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_categories_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    CONSTRAINT fk_categories_parent FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE suppliers (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT UNSIGNED NOT NULL,
    name VARCHAR(150) NOT NULL,
    contact_name VARCHAR(150) NULL,
    phone VARCHAR(20) NULL,
    email VARCHAR(150) NULL,
    address VARCHAR(255) NULL,
    tax_code VARCHAR(50) NULL,
    notes VARCHAR(500) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_suppliers_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE customers (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT UNSIGNED NOT NULL,
    customer_code VARCHAR(50) NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    phone VARCHAR(20) NULL,
    email VARCHAR(150) NULL,
    gender VARCHAR(10) NULL,
    date_of_birth DATE NULL,
    address VARCHAR(255) NULL,
    total_points INT NOT NULL DEFAULT 0,
    total_spent DECIMAL(15,2) NOT NULL DEFAULT 0,
    last_order_at DATETIME NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_customers_store_code UNIQUE (store_id, customer_code),
    CONSTRAINT fk_customers_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE products (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT UNSIGNED NOT NULL,
    category_id BIGINT UNSIGNED NULL,
    supplier_id BIGINT UNSIGNED NULL,
    sku VARCHAR(100) NOT NULL,
    name VARCHAR(200) NOT NULL,
    slug VARCHAR(220) NULL,
    barcode VARCHAR(100) NULL,
    unit_name VARCHAR(50) NOT NULL DEFAULT 'pcs',
    description TEXT NULL,
    cost_price DECIMAL(15,2) NOT NULL DEFAULT 0,
    selling_price DECIMAL(15,2) NOT NULL DEFAULT 0,
    track_inventory TINYINT(1) NOT NULL DEFAULT 1,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_products_store_sku UNIQUE (store_id, sku),
    CONSTRAINT uk_products_store_barcode UNIQUE (store_id, barcode),
    CONSTRAINT fk_products_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    CONSTRAINT fk_products_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE product_variants (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT UNSIGNED NOT NULL,
    sku VARCHAR(100) NOT NULL,
    barcode VARCHAR(100) NULL,
    variant_name VARCHAR(150) NOT NULL,
    option1_name VARCHAR(50) NULL,
    option1_value VARCHAR(100) NULL,
    option2_name VARCHAR(50) NULL,
    option2_value VARCHAR(100) NULL,
    cost_price DECIMAL(15,2) NOT NULL DEFAULT 0,
    selling_price DECIMAL(15,2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_product_variants_product_sku UNIQUE (product_id, sku),
    CONSTRAINT uk_product_variants_product_barcode UNIQUE (product_id, barcode),
    CONSTRAINT fk_product_variants_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE inventories (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT UNSIGNED NOT NULL,
    branch_id BIGINT UNSIGNED NOT NULL,
    product_id BIGINT UNSIGNED NOT NULL,
    variant_id BIGINT UNSIGNED NULL,
    quantity DECIMAL(15,2) NOT NULL DEFAULT 0,
    reserved_quantity DECIMAL(15,2) NOT NULL DEFAULT 0,
    min_quantity DECIMAL(15,2) NOT NULL DEFAULT 0,
    max_quantity DECIMAL(15,2) NULL,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_inventories_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    CONSTRAINT fk_inventories_branch FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE CASCADE,
    CONSTRAINT fk_inventories_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT fk_inventories_variant FOREIGN KEY (variant_id) REFERENCES product_variants(id) ON DELETE SET NULL,
    INDEX idx_inventories_lookup (store_id, branch_id, product_id, variant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE inventory_movements (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT UNSIGNED NOT NULL,
    branch_id BIGINT UNSIGNED NOT NULL,
    product_id BIGINT UNSIGNED NOT NULL,
    variant_id BIGINT UNSIGNED NULL,
    movement_type VARCHAR(30) NOT NULL,
    reference_type VARCHAR(50) NULL,
    reference_id BIGINT UNSIGNED NULL,
    delta_quantity DECIMAL(15,2) NOT NULL,
    before_quantity DECIMAL(15,2) NOT NULL,
    after_quantity DECIMAL(15,2) NOT NULL,
    note VARCHAR(255) NULL,
    created_by BIGINT UNSIGNED NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_inventory_movements_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    CONSTRAINT fk_inventory_movements_branch FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE RESTRICT,
    CONSTRAINT fk_inventory_movements_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT,
    CONSTRAINT fk_inventory_movements_variant FOREIGN KEY (variant_id) REFERENCES product_variants(id) ON DELETE SET NULL,
    CONSTRAINT fk_inventory_movements_user FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_inventory_movements_lookup (store_id, branch_id, product_id, variant_id, created_at),
    INDEX idx_inventory_movements_ref (reference_type, reference_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE purchase_orders (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT UNSIGNED NOT NULL,
    branch_id BIGINT UNSIGNED NULL,
    supplier_id BIGINT UNSIGNED NOT NULL,
    po_number VARCHAR(50) NOT NULL,
    order_date DATETIME NOT NULL,
    expected_date DATETIME NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    subtotal DECIMAL(15,2) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
    tax_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
    total_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
    notes VARCHAR(500) NULL,
    created_by BIGINT UNSIGNED NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_purchase_orders_number UNIQUE (po_number),
    CONSTRAINT fk_purchase_orders_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    CONSTRAINT fk_purchase_orders_branch FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE SET NULL,
    CONSTRAINT fk_purchase_orders_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE RESTRICT,
    CONSTRAINT fk_purchase_orders_creator FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE purchase_order_items (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    purchase_order_id BIGINT UNSIGNED NOT NULL,
    product_id BIGINT UNSIGNED NOT NULL,
    variant_id BIGINT UNSIGNED NULL,
    quantity DECIMAL(15,2) NOT NULL,
    received_quantity DECIMAL(15,2) NOT NULL DEFAULT 0,
    cost_price DECIMAL(15,2) NOT NULL,
    line_total DECIMAL(15,2) NOT NULL,
    CONSTRAINT fk_purchase_order_items_order FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_purchase_order_items_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT,
    CONSTRAINT fk_purchase_order_items_variant FOREIGN KEY (variant_id) REFERENCES product_variants(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE sales_orders (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT UNSIGNED NOT NULL,
    branch_id BIGINT UNSIGNED NOT NULL,
    customer_id BIGINT UNSIGNED NULL,
    order_number VARCHAR(50) NOT NULL,
    order_source VARCHAR(30) NOT NULL DEFAULT 'POS',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    subtotal DECIMAL(15,2) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
    tax_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
    shipping_fee DECIMAL(15,2) NOT NULL DEFAULT 0,
    total_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
    paid_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
    notes VARCHAR(500) NULL,
    sold_by BIGINT UNSIGNED NULL,
    ordered_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_sales_orders_number UNIQUE (order_number),
    CONSTRAINT fk_sales_orders_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    CONSTRAINT fk_sales_orders_branch FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE RESTRICT,
    CONSTRAINT fk_sales_orders_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE SET NULL,
    CONSTRAINT fk_sales_orders_sold_by FOREIGN KEY (sold_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_sales_orders_branch_date (branch_id, ordered_at),
    INDEX idx_sales_orders_customer (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE sales_order_items (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    sales_order_id BIGINT UNSIGNED NOT NULL,
    product_id BIGINT UNSIGNED NOT NULL,
    variant_id BIGINT UNSIGNED NULL,
    product_name VARCHAR(200) NOT NULL,
    sku VARCHAR(100) NOT NULL,
    unit_price DECIMAL(15,2) NOT NULL,
    quantity DECIMAL(15,2) NOT NULL,
    discount_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
    line_total DECIMAL(15,2) NOT NULL,
    CONSTRAINT fk_sales_order_items_order FOREIGN KEY (sales_order_id) REFERENCES sales_orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_sales_order_items_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT,
    CONSTRAINT fk_sales_order_items_variant FOREIGN KEY (variant_id) REFERENCES product_variants(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE payments (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT UNSIGNED NOT NULL,
    branch_id BIGINT UNSIGNED NOT NULL,
    sales_order_id BIGINT UNSIGNED NOT NULL,
    payment_code VARCHAR(50) NOT NULL,
    payment_method VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PAID',
    amount DECIMAL(15,2) NOT NULL,
    paid_at DATETIME NOT NULL,
    transaction_ref VARCHAR(100) NULL,
    notes VARCHAR(255) NULL,
    created_by BIGINT UNSIGNED NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_payments_code UNIQUE (payment_code),
    CONSTRAINT fk_payments_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    CONSTRAINT fk_payments_branch FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE RESTRICT,
    CONSTRAINT fk_payments_order FOREIGN KEY (sales_order_id) REFERENCES sales_orders(id) ON DELETE RESTRICT,
    CONSTRAINT fk_payments_user FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE shifts (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT UNSIGNED NOT NULL,
    branch_id BIGINT UNSIGNED NOT NULL,
    cashier_user_id BIGINT UNSIGNED NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    opening_cash DECIMAL(15,2) NOT NULL DEFAULT 0,
    closing_cash DECIMAL(15,2) NULL,
    opened_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    closed_at DATETIME NULL,
    note VARCHAR(500) NULL,
    CONSTRAINT fk_shifts_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    CONSTRAINT fk_shifts_branch FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE RESTRICT,
    CONSTRAINT fk_shifts_cashier FOREIGN KEY (cashier_user_id) REFERENCES users(id) ON DELETE RESTRICT,
    INDEX idx_shifts_lookup (store_id, branch_id, status, opened_at),
    INDEX idx_shifts_cashier (store_id, cashier_user_id, opened_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE shipments (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT UNSIGNED NOT NULL,
    branch_id BIGINT UNSIGNED NOT NULL,
    sales_order_id BIGINT UNSIGNED NOT NULL,
    shipment_code VARCHAR(50) NOT NULL,
    carrier_name VARCHAR(100) NULL,
    service_name VARCHAR(100) NULL,
    tracking_number VARCHAR(100) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'READY_TO_SHIP',
    receiver_name VARCHAR(150) NOT NULL,
    receiver_phone VARCHAR(20) NOT NULL,
    receiver_address VARCHAR(255) NOT NULL,
    shipping_fee DECIMAL(15,2) NOT NULL DEFAULT 0,
    cod_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
    shipped_at DATETIME NULL,
    delivered_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_shipments_code UNIQUE (shipment_code),
    CONSTRAINT uk_shipments_tracking_number UNIQUE (tracking_number),
    CONSTRAINT fk_shipments_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    CONSTRAINT fk_shipments_branch FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE RESTRICT,
    CONSTRAINT fk_shipments_order FOREIGN KEY (sales_order_id) REFERENCES sales_orders(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE cashbook_entries (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT UNSIGNED NOT NULL,
    branch_id BIGINT UNSIGNED NULL,
    entry_type VARCHAR(20) NOT NULL,
    category VARCHAR(100) NOT NULL,
    reference_type VARCHAR(50) NULL,
    reference_id BIGINT UNSIGNED NULL,
    amount DECIMAL(15,2) NOT NULL,
    description VARCHAR(255) NULL,
    occurred_at DATETIME NOT NULL,
    created_by BIGINT UNSIGNED NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cashbook_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    CONSTRAINT fk_cashbook_branch FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE SET NULL,
    CONSTRAINT fk_cashbook_user FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_cashbook_occurred_at (occurred_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE integration_channels (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT UNSIGNED NOT NULL,
    channel_type VARCHAR(30) NOT NULL,
    channel_name VARCHAR(150) NOT NULL,
    channel_code VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    config_json JSON NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_integration_channels_store_code UNIQUE (store_id, channel_code),
    CONSTRAINT fk_integration_channels_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE integration_product_mappings (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT UNSIGNED NOT NULL,
    channel_id BIGINT UNSIGNED NOT NULL,
    product_id BIGINT UNSIGNED NOT NULL,
    variant_id BIGINT UNSIGNED NULL,
    external_product_id VARCHAR(100) NULL,
    external_variant_id VARCHAR(100) NULL,
    external_sku VARCHAR(100) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_integration_product_mappings UNIQUE (channel_id, product_id, variant_id),
    CONSTRAINT fk_integration_product_mappings_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    CONSTRAINT fk_integration_product_mappings_channel FOREIGN KEY (channel_id) REFERENCES integration_channels(id) ON DELETE CASCADE,
    CONSTRAINT fk_integration_product_mappings_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT fk_integration_product_mappings_variant FOREIGN KEY (variant_id) REFERENCES product_variants(id) ON DELETE SET NULL,
    INDEX idx_integration_product_mappings_lookup (store_id, channel_id, product_id, variant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE promotions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT UNSIGNED NOT NULL,
    name VARCHAR(150) NOT NULL,
    code VARCHAR(50) NOT NULL,
    promotion_type VARCHAR(30) NOT NULL,
    value_type VARCHAR(20) NOT NULL,
    value_amount DECIMAL(15,2) NOT NULL,
    min_order_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
    max_discount_amount DECIMAL(15,2) NULL,
    start_at DATETIME NOT NULL,
    end_at DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_promotions_store_code UNIQUE (store_id, code),
    CONSTRAINT fk_promotions_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE vouchers (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT UNSIGNED NOT NULL,
    promotion_id BIGINT UNSIGNED NULL,
    code VARCHAR(50) NOT NULL,
    discount_type VARCHAR(20) NOT NULL,
    discount_value DECIMAL(15,2) NOT NULL,
    min_order_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
    max_discount_amount DECIMAL(15,2) NULL,
    usage_limit INT NOT NULL DEFAULT 1,
    used_count INT NOT NULL DEFAULT 0,
    start_at DATETIME NOT NULL,
    end_at DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_vouchers_store_code UNIQUE (store_id, code),
    CONSTRAINT fk_vouchers_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    CONSTRAINT fk_vouchers_promotion FOREIGN KEY (promotion_id) REFERENCES promotions(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE loyalty_accounts (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT UNSIGNED NOT NULL,
    customer_id BIGINT UNSIGNED NOT NULL,
    current_points INT NOT NULL DEFAULT 0,
    lifetime_points INT NOT NULL DEFAULT 0,
    tier_name VARCHAR(50) NOT NULL DEFAULT 'SILVER',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_loyalty_customer UNIQUE (customer_id),
    CONSTRAINT fk_loyalty_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    CONSTRAINT fk_loyalty_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE loyalty_transactions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    loyalty_account_id BIGINT UNSIGNED NOT NULL,
    reference_type VARCHAR(50) NULL,
    reference_id BIGINT UNSIGNED NULL,
    points_change INT NOT NULL,
    description VARCHAR(255) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_loyalty_transactions_account FOREIGN KEY (loyalty_account_id) REFERENCES loyalty_accounts(id) ON DELETE CASCADE,
    INDEX idx_loyalty_transactions_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- NOTE:
-- - audit_logs schema used by current backend is defined in V2_1 section below.
-- - We intentionally DO NOT create audit_logs here in V1 section to keep this all-in-one SQL consistent.

CREATE TABLE settings (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT UNSIGNED NOT NULL,
    setting_key VARCHAR(100) NOT NULL,
    setting_value TEXT NULL,
    description VARCHAR(255) NULL,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_settings_store_key UNIQUE (store_id, setting_key),
    CONSTRAINT fk_settings_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE notifications (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT UNSIGNED NULL,
    user_id BIGINT UNSIGNED NOT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(30) NOT NULL DEFAULT 'SYSTEM',
    is_read TINYINT(1) NOT NULL DEFAULT 0,
    read_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_notifications_user_read (user_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE OR REPLACE VIEW vw_sales_daily_summary AS
SELECT
    so.store_id,
    so.branch_id,
    DATE(so.ordered_at) AS sale_date,
    COUNT(*) AS total_orders,
    COALESCE(SUM(so.total_amount), 0) AS gross_revenue,
    COALESCE(SUM(so.paid_amount), 0) AS collected_amount
FROM sales_orders so
GROUP BY so.store_id, so.branch_id, DATE(so.ordered_at);

CREATE OR REPLACE VIEW vw_inventory_overview AS
SELECT
    i.store_id,
    i.branch_id,
    p.id AS product_id,
    p.sku,
    p.name AS product_name,
    COALESCE(pv.variant_name, '-') AS variant_name,
    i.quantity,
    i.reserved_quantity,
    (i.quantity - i.reserved_quantity) AS available_quantity,
    i.min_quantity,
    i.max_quantity
FROM inventories i
JOIN products p ON p.id = i.product_id
LEFT JOIN product_variants pv ON pv.id = i.variant_id;

-- =========================
-- V1_3__sales_orders_discount_source.sql
-- =========================

ALTER TABLE sales_orders
  ADD COLUMN applied_voucher_id BIGINT NULL,
  ADD COLUMN applied_voucher_code VARCHAR(50) NULL,
  ADD COLUMN applied_promotion_id BIGINT NULL,
  ADD COLUMN applied_promotion_code VARCHAR(50) NULL,
  ADD COLUMN discount_source VARCHAR(20) NULL;

-- =========================
-- V2_1__create_audit_logs.sql (override V1 audit_logs)
-- =========================

DROP TABLE IF EXISTS audit_logs;

CREATE TABLE IF NOT EXISTS audit_logs (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  store_id BIGINT NOT NULL,
  branch_id BIGINT NULL,
  actor_user_id BIGINT NULL,
  actor_username VARCHAR(100) NULL,
  module VARCHAR(50) NOT NULL,
  action VARCHAR(50) NOT NULL,
  entity_type VARCHAR(100) NULL,
  entity_id BIGINT NULL,
  message VARCHAR(500) NULL,
  ip VARCHAR(50) NULL,
  user_agent VARCHAR(255) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- MySQL-compatible idempotent index creation (no CREATE INDEX IF NOT EXISTS in MySQL)
SET @sql := (
  SELECT IF(
    EXISTS(
      SELECT 1
      FROM information_schema.statistics
      WHERE table_schema = DATABASE()
        AND table_name = 'audit_logs'
        AND index_name = 'idx_audit_logs_store_created'
    ),
    'SELECT 1',
    'CREATE INDEX idx_audit_logs_store_created ON audit_logs(store_id, created_at)'
  )
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := (
  SELECT IF(
    EXISTS(
      SELECT 1
      FROM information_schema.statistics
      WHERE table_schema = DATABASE()
        AND table_name = 'audit_logs'
        AND index_name = 'idx_audit_logs_store_module'
    ),
    'SELECT 1',
    'CREATE INDEX idx_audit_logs_store_module ON audit_logs(store_id, module)'
  )
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := (
  SELECT IF(
    EXISTS(
      SELECT 1
      FROM information_schema.statistics
      WHERE table_schema = DATABASE()
        AND table_name = 'audit_logs'
        AND index_name = 'idx_audit_logs_store_actor'
    ),
    'SELECT 1',
    'CREATE INDEX idx_audit_logs_store_actor ON audit_logs(store_id, actor_user_id)'
  )
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := (
  SELECT IF(
    EXISTS(
      SELECT 1
      FROM information_schema.statistics
      WHERE table_schema = DATABASE()
        AND table_name = 'audit_logs'
        AND index_name = 'idx_audit_logs_store_branch'
    ),
    'SELECT 1',
    'CREATE INDEX idx_audit_logs_store_branch ON audit_logs(store_id, branch_id)'
  )
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- =========================
-- V2_2__purchase_returns.sql
-- =========================

CREATE TABLE IF NOT EXISTS purchase_returns (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  store_id BIGINT NOT NULL,
  branch_id BIGINT NOT NULL,
  supplier_id BIGINT NOT NULL,
  purchase_order_id BIGINT NOT NULL,
  return_number VARCHAR(50) NOT NULL,
  status VARCHAR(20) NOT NULL,
  total_quantity DECIMAL(15,2) NOT NULL DEFAULT 0,
  total_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
  reason VARCHAR(500) NULL,
  created_by BIGINT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS purchase_return_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  purchase_return_id BIGINT NOT NULL,
  purchase_order_item_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  variant_id BIGINT NULL,
  quantity DECIMAL(15,2) NOT NULL,
  unit_cost DECIMAL(15,2) NOT NULL,
  line_total DECIMAL(15,2) NOT NULL
);

SET @sql := (
  SELECT IF(
    EXISTS(
      SELECT 1
      FROM information_schema.statistics
      WHERE table_schema = DATABASE()
        AND table_name = 'purchase_returns'
        AND index_name = 'idx_purchase_returns_store_po'
    ),
    'SELECT 1',
    'CREATE INDEX idx_purchase_returns_store_po ON purchase_returns(store_id, purchase_order_id)'
  )
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := (
  SELECT IF(
    EXISTS(
      SELECT 1
      FROM information_schema.statistics
      WHERE table_schema = DATABASE()
        AND table_name = 'purchase_return_items'
        AND index_name = 'idx_purchase_return_items_pr'
    ),
    'SELECT 1',
    'CREATE INDEX idx_purchase_return_items_pr ON purchase_return_items(purchase_return_id)'
  )
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := (
  SELECT IF(
    EXISTS(
      SELECT 1
      FROM information_schema.statistics
      WHERE table_schema = DATABASE()
        AND table_name = 'purchase_return_items'
        AND index_name = 'idx_purchase_return_items_poi'
    ),
    'SELECT 1',
    'CREATE INDEX idx_purchase_return_items_poi ON purchase_return_items(purchase_order_item_id)'
  )
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- =========================
-- V3_1__import_jobs.sql
-- =========================

CREATE TABLE IF NOT EXISTS import_jobs (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  store_id BIGINT NOT NULL,
  type VARCHAR(50) NOT NULL,
  status VARCHAR(30) NOT NULL,
  original_filename VARCHAR(255),
  content_type VARCHAR(100),
  created_by BIGINT,
  total_rows INT,
  success_rows INT,
  failed_rows INT,
  error_message VARCHAR(1000),
  result_file_path VARCHAR(500),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  started_at TIMESTAMP NULL,
  finished_at TIMESTAMP NULL,
  INDEX idx_import_jobs_store_created (store_id, created_at),
  INDEX idx_import_jobs_store_status (store_id, status)
);

-- =========================
-- V3_2__e_invoices.sql
-- =========================

CREATE TABLE IF NOT EXISTS e_invoices (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  store_id BIGINT NOT NULL,
  sales_order_id BIGINT NOT NULL,
  status VARCHAR(30) NOT NULL,
  provider_name VARCHAR(50),
  provider_invoice_id VARCHAR(100),
  invoice_number VARCHAR(50),
  buyer_name VARCHAR(200),
  buyer_tax_code VARCHAR(50),
  buyer_address VARCHAR(255),
  buyer_email VARCHAR(150),
  subtotal DECIMAL(15,2) NOT NULL,
  tax_amount DECIMAL(15,2) NOT NULL,
  total_amount DECIMAL(15,2) NOT NULL,
  issued_at TIMESTAMP NULL,
  error_message VARCHAR(1000),
  created_by BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_e_invoices_store_order (store_id, sales_order_id),
  INDEX idx_e_invoices_store_status (store_id, status),
  INDEX idx_e_invoices_store_created (store_id, created_at)
);

-- =========================
-- V3_3__online_orders.sql
-- =========================

CREATE TABLE IF NOT EXISTS online_orders (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  store_id BIGINT NOT NULL,
  channel_id BIGINT NOT NULL,
  external_order_id VARCHAR(100) NOT NULL,
  status VARCHAR(30) NOT NULL,
  external_order_number VARCHAR(100),
  buyer_name VARCHAR(200),
  buyer_phone VARCHAR(30),
  shipping_address VARCHAR(255),
  subtotal DECIMAL(15,2) NOT NULL,
  discount_amount DECIMAL(15,2) NOT NULL,
  shipping_fee DECIMAL(15,2) NOT NULL,
  total_amount DECIMAL(15,2) NOT NULL,
  items_json JSON,
  raw_payload JSON,
  synced_at TIMESTAMP NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_online_orders_store_channel_ext (store_id, channel_id, external_order_id),
  INDEX idx_online_orders_store_created (store_id, created_at),
  INDEX idx_online_orders_store_status (store_id, status),
  INDEX idx_online_orders_store_channel (store_id, channel_id)
);

-- ============================================================
-- RBAC Seed (roles/permissions/role_permissions)
-- Aligned with backend seed: RbacSeedRunner (2026-04-10)
-- Idempotent: safe to run multiple times
-- ============================================================

-- Roles
INSERT INTO roles (name, code, description, is_system, status)
VALUES
  ('System Admin',   'SYSTEM_ADMIN',  'Platform/system administrator', 1, 'ACTIVE'),
  ('Store Owner',    'STORE_OWNER',   'Store owner with full access to store data', 1, 'ACTIVE'),
  ('Store Manager',  'STORE_MANAGER', 'Compatibility role (alias of STORE_OWNER)', 1, 'ACTIVE'),
  ('Branch Manager', 'BRANCH_MANAGER','Manage operations within a branch', 1, 'ACTIVE'),
  ('Cashier',        'CASHIER',       'POS cashier / sales staff', 1, 'ACTIVE'),
  ('Warehouse Staff','WAREHOUSE',     'Warehouse operations (purchasing & inventory)', 1, 'ACTIVE'),
  ('Accountant / Cashier (Finance)', 'ACCOUNTANT', 'Finance operations', 1, 'ACTIVE'),
  ('CSKH / Online Sales', 'CSKH_ONLINE', 'Customer care / online sales', 1, 'ACTIVE'),
  ('Admin',          'ADMIN',         'Compatibility admin role', 1, 'ACTIVE'),
  ('Super Admin',    'SUPER_ADMIN',   'Compatibility super admin role', 1, 'ACTIVE')
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  description = VALUES(description),
  is_system = VALUES(is_system),
  status = VALUES(status);

-- Permissions
INSERT INTO permissions (code, name, module_name, description)
VALUES
  ('AUDITLOG_READ', 'Audit Log - Read', 'auditlog', 'View audit logs'),

  ('USER_READ', 'User - Read', 'user', 'View users'),
  ('USER_WRITE', 'User - Write', 'user', 'Create/update users'),
  ('USER_RESET_PASSWORD', 'User - Reset Password', 'user', 'Reset user passwords'),

  ('ROLE_READ', 'Role - Read', 'role', 'View roles'),
  ('ROLE_WRITE', 'Role - Write', 'role', 'Create/update roles and mappings'),

  ('PERMISSION_READ', 'Permission - Read', 'permission', 'View permissions catalog'),

  ('STORE_READ', 'Store - Read', 'store', 'View store settings'),
  ('STORE_WRITE', 'Store - Write', 'store', 'Update store settings'),
  ('BRANCH_READ', 'Branch - Read', 'branch', 'View branches'),
  ('BRANCH_WRITE', 'Branch - Write', 'branch', 'Create/update branches'),
  ('EMPLOYEE_READ', 'Employee - Read', 'employee', 'View employees'),
  ('EMPLOYEE_WRITE', 'Employee - Write', 'employee', 'Create/update employees'),

  ('CATEGORY_READ', 'Category - Read', 'category', 'View categories'),
  ('CATEGORY_WRITE', 'Category - Write', 'category', 'Create/update categories'),
  ('PRODUCT_READ', 'Product - Read', 'product', 'View products'),
  ('PRODUCT_WRITE', 'Product - Write', 'product', 'Create/update/disable products'),
  ('PRODUCT_IMPORT', 'Product - Import', 'product', 'Import products from file'),
  ('PRODUCT_EXPORT', 'Product - Export', 'product', 'Export products to file'),

  ('SUPPLIER_READ', 'Supplier - Read', 'supplier', 'View suppliers'),
  ('SUPPLIER_WRITE', 'Supplier - Write', 'supplier', 'Create/update suppliers'),

  ('INVENTORY_READ', 'Inventory - Read', 'inventory', 'View inventory'),
  ('INVENTORY_ADJUST', 'Inventory - Adjust', 'inventory', 'Adjust inventory quantities'),

  ('PURCHASE_READ', 'Purchase Order - Read', 'purchaseorder', 'View purchase orders'),
  ('PURCHASE_CREATE', 'Purchase Order - Create', 'purchaseorder', 'Create purchase orders'),
  ('PURCHASE_RECEIVE', 'Purchase Order - Receive', 'purchaseorder', 'Receive goods and post inventory'),
  ('PURCHASE_CANCEL', 'Purchase Order - Cancel', 'purchaseorder', 'Cancel purchase orders'),
  ('PURCHASE_RETURN', 'Purchase Order - Return', 'purchaseorder', 'Return goods to supplier'),

  ('POS_ORDER_READ', 'POS Order - Read', 'salesorder', 'View sales orders'),
  ('POS_ORDER_CREATE', 'POS Order - Create', 'salesorder', 'Create sales orders'),
  ('POS_ORDER_COMPLETE', 'POS Order - Complete', 'salesorder', 'Complete sales orders'),
  ('POS_ORDER_RETURN', 'POS Return - Create', 'returnorder', 'Create return orders'),
  ('POS_ORDER_HOLD', 'POS Order - Hold', 'salesorder', 'Hold/reserve orders'),
  ('POS_ORDER_DISCOUNT', 'POS Order - Discount', 'salesorder', 'Apply voucher/promotion to orders'),

  ('PAYMENT_CREATE', 'Payment - Create', 'payment', 'Create payments for orders'),
  ('PAYMENT_READ', 'Payment - Read', 'payment', 'View payments'),

  ('CUSTOMER_READ', 'Customer - Read', 'customer', 'View customers'),
  ('CUSTOMER_WRITE', 'Customer - Write', 'customer', 'Create/update customers'),
  ('LOYALTY_READ', 'Loyalty - Read', 'loyalty', 'View loyalty accounts/transactions'),
  ('LOYALTY_REDEEM', 'Loyalty - Redeem', 'loyalty', 'Redeem loyalty points'),

  ('CASHBOOK_READ', 'Cashbook - Read', 'cashbook', 'View cashbook entries'),
  ('CASHBOOK_WRITE', 'Cashbook - Write', 'cashbook', 'Create manual cashbook entries'),
  ('DEBT_READ', 'Debt - Read', 'finance', 'View customer/supplier debts'),
  ('REPORT_READ', 'Report - Read', 'report', 'View reports'),
  ('DASHBOARD_READ', 'Dashboard - Read', 'dashboard', 'View dashboard widgets'),

  ('IMPORT_JOB_READ', 'Import Job - Read', 'importjob', 'View import job status/result'),

  ('EINVOICE_ISSUE', 'E-Invoice - Issue', 'einvoice', 'Issue e-invoice for sales orders'),
  ('EINVOICE_READ', 'E-Invoice - Read', 'einvoice', 'View issued e-invoices'),

  ('INTEGRATION_READ', 'Integration - Read', 'integration', 'View integration channels and mappings'),
  ('INTEGRATION_WRITE', 'Integration - Write', 'integration', 'Create/update integration channels and mappings'),
  ('INTEGRATION_SYNC_ORDERS', 'Integration - Sync Orders', 'integration', 'Trigger sync orders from channel'),
  ('ONLINE_ORDER_READ', 'Online Order - Read', 'integration', 'View online orders'),

  ('SHIPMENT_READ', 'Shipment - Read', 'shipment', 'View shipments'),
  ('SHIPMENT_WRITE', 'Shipment - Write', 'shipment', 'Create/update shipments and statuses'),

  ('SHIFT_READ', 'Shift - Read', 'shift', 'View current/previous shifts'),
  ('SHIFT_OPEN', 'Shift - Open', 'shift', 'Open a shift at a branch'),
  ('SHIFT_CLOSE', 'Shift - Close', 'shift', 'Close a shift at a branch')
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  module_name = VALUES(module_name),
  description = VALUES(description);

-- Role -> permission mappings (use INSERT IGNORE for idempotency)

-- SYSTEM_ADMIN
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN ('USER_READ','USER_WRITE','USER_RESET_PASSWORD','ROLE_READ','ROLE_WRITE','PERMISSION_READ','AUDITLOG_READ')
WHERE r.code = 'SYSTEM_ADMIN';

-- STORE_OWNER (all business perms; exclude platform user/role/permission)
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN (
  'AUDITLOG_READ',
  'STORE_READ','STORE_WRITE',
  'BRANCH_READ','BRANCH_WRITE',
  'EMPLOYEE_READ','EMPLOYEE_WRITE',
  'CATEGORY_READ','CATEGORY_WRITE',
  'PRODUCT_READ','PRODUCT_WRITE','PRODUCT_IMPORT','PRODUCT_EXPORT',
  'SUPPLIER_READ','SUPPLIER_WRITE',
  'INVENTORY_READ','INVENTORY_ADJUST',
  'PURCHASE_READ','PURCHASE_CREATE','PURCHASE_RECEIVE','PURCHASE_CANCEL','PURCHASE_RETURN',
  'POS_ORDER_READ','POS_ORDER_CREATE','POS_ORDER_COMPLETE','POS_ORDER_RETURN','POS_ORDER_HOLD','POS_ORDER_DISCOUNT',
  'PAYMENT_CREATE','PAYMENT_READ',
  'CUSTOMER_READ','CUSTOMER_WRITE',
  'LOYALTY_READ','LOYALTY_REDEEM',
  'CASHBOOK_READ','CASHBOOK_WRITE','DEBT_READ',
  'REPORT_READ','DASHBOARD_READ',
  'IMPORT_JOB_READ',
  'EINVOICE_ISSUE','EINVOICE_READ',
  'INTEGRATION_READ','INTEGRATION_WRITE','INTEGRATION_SYNC_ORDERS','ONLINE_ORDER_READ',
  'SHIPMENT_READ','SHIPMENT_WRITE',
  'SHIFT_READ','SHIFT_OPEN','SHIFT_CLOSE'
)
WHERE r.code = 'STORE_OWNER';

-- STORE_MANAGER (STORE_OWNER minus sensitive write ops)
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN (
  'AUDITLOG_READ',
  'STORE_READ',
  'BRANCH_READ','BRANCH_WRITE',
  'EMPLOYEE_READ','EMPLOYEE_WRITE',
  'CATEGORY_READ','CATEGORY_WRITE',
  'PRODUCT_READ','PRODUCT_WRITE',
  'SUPPLIER_READ','SUPPLIER_WRITE',
  'INVENTORY_READ','INVENTORY_ADJUST',
  'PURCHASE_READ','PURCHASE_CREATE','PURCHASE_RECEIVE','PURCHASE_CANCEL','PURCHASE_RETURN',
  'POS_ORDER_READ','POS_ORDER_CREATE','POS_ORDER_COMPLETE','POS_ORDER_RETURN','POS_ORDER_HOLD','POS_ORDER_DISCOUNT',
  'PAYMENT_CREATE','PAYMENT_READ',
  'CUSTOMER_READ','CUSTOMER_WRITE',
  'LOYALTY_READ','LOYALTY_REDEEM',
  'CASHBOOK_READ','DEBT_READ',
  'REPORT_READ','DASHBOARD_READ',
  'IMPORT_JOB_READ',
  'EINVOICE_READ',
  'INTEGRATION_READ','ONLINE_ORDER_READ',
  'SHIPMENT_READ','SHIPMENT_WRITE',
  'SHIFT_READ','SHIFT_OPEN','SHIFT_CLOSE'
)
WHERE r.code = 'STORE_MANAGER';

-- BRANCH_MANAGER
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN (
  'STORE_READ',
  'BRANCH_READ',
  'EMPLOYEE_READ',
  'CATEGORY_READ','CATEGORY_WRITE',
  'PRODUCT_READ','PRODUCT_WRITE','PRODUCT_IMPORT','PRODUCT_EXPORT',
  'SUPPLIER_READ','SUPPLIER_WRITE',
  'INVENTORY_READ','INVENTORY_ADJUST',
  'PURCHASE_READ','PURCHASE_CREATE','PURCHASE_RECEIVE',
  'POS_ORDER_READ','POS_ORDER_CREATE','POS_ORDER_COMPLETE','POS_ORDER_RETURN','POS_ORDER_HOLD','POS_ORDER_DISCOUNT',
  'PAYMENT_CREATE','PAYMENT_READ',
  'CUSTOMER_READ','CUSTOMER_WRITE',
  'LOYALTY_READ','LOYALTY_REDEEM',
  'INTEGRATION_READ','INTEGRATION_WRITE','INTEGRATION_SYNC_ORDERS','ONLINE_ORDER_READ',
  'CASHBOOK_READ','CASHBOOK_WRITE','DEBT_READ',
  'REPORT_READ','DASHBOARD_READ',
  'SHIFT_READ','SHIFT_OPEN','SHIFT_CLOSE',
  'IMPORT_JOB_READ',
  'EINVOICE_ISSUE','EINVOICE_READ',
  'SHIPMENT_READ','SHIPMENT_WRITE'
)
WHERE r.code = 'BRANCH_MANAGER';

-- CASHIER
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN (
  'CATEGORY_READ',
  'PRODUCT_READ',
  'INVENTORY_READ',
  'POS_ORDER_READ','POS_ORDER_CREATE','POS_ORDER_COMPLETE','POS_ORDER_RETURN',
  'POS_ORDER_DISCOUNT',
  'PAYMENT_CREATE','PAYMENT_READ',
  'CUSTOMER_READ','CUSTOMER_WRITE',
  'LOYALTY_READ','LOYALTY_REDEEM',
  'DASHBOARD_READ',
  'SHIFT_READ','SHIFT_OPEN','SHIFT_CLOSE',
  'SHIPMENT_READ','SHIPMENT_WRITE'
)
WHERE r.code = 'CASHIER';

-- WAREHOUSE
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN (
  'SUPPLIER_READ','SUPPLIER_WRITE',
  'PRODUCT_READ','PRODUCT_IMPORT','PRODUCT_EXPORT',
  'INVENTORY_READ','INVENTORY_ADJUST',
  'PURCHASE_READ','PURCHASE_CREATE','PURCHASE_RECEIVE',
  'SHIFT_READ',
  'ONLINE_ORDER_READ',
  'REPORT_READ','DASHBOARD_READ',
  'SHIPMENT_READ'
)
WHERE r.code = 'WAREHOUSE';

-- ACCOUNTANT
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN (
  'PAYMENT_READ',
  'CASHBOOK_READ','CASHBOOK_WRITE','DEBT_READ',
  'SHIFT_READ',
  'REPORT_READ','DASHBOARD_READ',
  'IMPORT_JOB_READ',
  'PRODUCT_EXPORT',
  'EINVOICE_ISSUE','EINVOICE_READ',
  'INTEGRATION_READ','ONLINE_ORDER_READ'
)
WHERE r.code = 'ACCOUNTANT';

-- CSKH_ONLINE
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN (
  'CUSTOMER_READ','CUSTOMER_WRITE',
  'LOYALTY_READ','LOYALTY_REDEEM',
  'POS_ORDER_READ',
  'POS_ORDER_DISCOUNT',
  'REPORT_READ','DASHBOARD_READ',
  'SHIPMENT_READ'
)
WHERE r.code = 'CSKH_ONLINE';

-- ADMIN (all perms)
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON 1=1
WHERE r.code = 'ADMIN';

-- SUPER_ADMIN (all perms)
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON 1=1
WHERE r.code = 'SUPER_ADMIN';

