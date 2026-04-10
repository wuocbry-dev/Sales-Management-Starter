-- ============================================================
-- Sales Management Starter - Sample Data
-- Purpose: create 1 demo user per RBAC role (+ demo store/branch/employees)
-- Requires: schema + RBAC seed already applied (see docs/Database/database.sql)
-- MySQL 8.0+
-- Idempotent: safe to run multiple times (uses INSERT IGNORE)
-- ============================================================

USE sales_management;

SET NAMES utf8mb4;

-- Common bcrypt hash for password: 12345678
-- (BCryptPasswordEncoder, strength 10)
SET @PW_12345678 := '$2a$10$nXaAZ4Y3QBIpumROvhxUh.sNm5bn1MD9Zdmuk7kPM6ngDBU/I.W36';

-- -------------------------
-- Users (one per role)
-- -------------------------
INSERT IGNORE INTO users (full_name, username, email, phone, password_hash, status)
VALUES
  ('System Admin',   'system_admin',   'system_admin@example.com',   '0900000001', @PW_12345678, 'ACTIVE'),
  ('Super Admin',    'super_admin',    'super_admin@example.com',    '0900000002', @PW_12345678, 'ACTIVE'),
  ('Admin',          'admin',          'admin@example.com',          '0900000003', @PW_12345678, 'ACTIVE'),
  ('Store Owner',    'store_owner',    'store_owner@example.com',    '0900000004', @PW_12345678, 'ACTIVE'),
  ('Store Manager',  'store_manager',  'store_manager@example.com',  '0900000005', @PW_12345678, 'ACTIVE'),
  ('Branch Manager', 'branch_manager', 'branch_manager@example.com', '0900000006', @PW_12345678, 'ACTIVE'),
  ('Cashier',        'cashier',        'cashier@example.com',        '0900000007', @PW_12345678, 'ACTIVE'),
  ('Warehouse',      'warehouse',      'warehouse@example.com',      '0900000008', @PW_12345678, 'ACTIVE'),
  ('Accountant',     'accountant',     'accountant@example.com',     '0900000009', @PW_12345678, 'ACTIVE'),
  ('CSKH Online',    'cskh_online',    'cskh_online@example.com',    '0900000010', @PW_12345678, 'ACTIVE');

SET @U_SYSTEM_ADMIN := (SELECT id FROM users WHERE username='system_admin' LIMIT 1);
SET @U_SUPER_ADMIN  := (SELECT id FROM users WHERE username='super_admin' LIMIT 1);
SET @U_ADMIN        := (SELECT id FROM users WHERE username='admin' LIMIT 1);
SET @U_STORE_OWNER  := (SELECT id FROM users WHERE username='store_owner' LIMIT 1);
SET @U_STORE_MGR    := (SELECT id FROM users WHERE username='store_manager' LIMIT 1);
SET @U_BRANCH_MGR   := (SELECT id FROM users WHERE username='branch_manager' LIMIT 1);
SET @U_CASHIER      := (SELECT id FROM users WHERE username='cashier' LIMIT 1);
SET @U_WAREHOUSE    := (SELECT id FROM users WHERE username='warehouse' LIMIT 1);
SET @U_ACCOUNTANT   := (SELECT id FROM users WHERE username='accountant' LIMIT 1);
SET @U_CSKH         := (SELECT id FROM users WHERE username='cskh_online' LIMIT 1);

-- -------------------------
-- Store + default branch
-- (owner resolved by store.owner_user_id)
-- -------------------------
INSERT IGNORE INTO stores (name, code, business_type, phone, email, address, tax_code, status, owner_user_id)
VALUES ('Demo Store', 'DEMO_STORE', 'RETAIL', '0900999999', 'demo_store@example.com', 'HCM City', NULL, 'ACTIVE', @U_STORE_OWNER);

SET @STORE_ID := (SELECT id FROM stores WHERE code='DEMO_STORE' LIMIT 1);

INSERT IGNORE INTO branches (store_id, name, code, phone, email, address, is_default, status)
VALUES (@STORE_ID, 'Chi nhanh mac dinh', 'BRANCH_DEMO', '0900888888', 'branch_demo@example.com', 'HCM City', 1, 'ACTIVE');

SET @BRANCH_ID := (SELECT id FROM branches WHERE store_id=@STORE_ID AND code='BRANCH_DEMO' LIMIT 1);

-- -------------------------
-- Employees (context for non-owner users)
-- (auth context resolved via employees.user_id)
-- -------------------------
INSERT IGNORE INTO employees (store_id, branch_id, user_id, employee_code, full_name, phone, email, position_name, salary, status)
VALUES
  (@STORE_ID, @BRANCH_ID, @U_STORE_MGR,  'EMP_STORE_MGR',  'Store Manager',  '0900000005', 'store_manager@example.com',  'Store Manager', 0, 'ACTIVE'),
  (@STORE_ID, @BRANCH_ID, @U_BRANCH_MGR, 'EMP_BRANCH_MGR', 'Branch Manager', '0900000006', 'branch_manager@example.com', 'Branch Manager', 0, 'ACTIVE'),
  (@STORE_ID, @BRANCH_ID, @U_CASHIER,    'EMP_CASHIER',    'Cashier',        '0900000007', 'cashier@example.com',        'Cashier', 0, 'ACTIVE'),
  (@STORE_ID, @BRANCH_ID, @U_WAREHOUSE,  'EMP_WAREHOUSE',  'Warehouse',      '0900000008', 'warehouse@example.com',      'Warehouse', 0, 'ACTIVE'),
  (@STORE_ID, @BRANCH_ID, @U_ACCOUNTANT, 'EMP_ACCOUNTANT', 'Accountant',     '0900000009', 'accountant@example.com',     'Accountant', 0, 'ACTIVE'),
  (@STORE_ID, @BRANCH_ID, @U_CSKH,       'EMP_CSKH',       'CSKH Online',    '0900000010', 'cskh_online@example.com',    'CSKH', 0, 'ACTIVE');

-- -------------------------
-- User -> roles
-- -------------------------
SET @R_SYSTEM_ADMIN := (SELECT id FROM roles WHERE code='SYSTEM_ADMIN' LIMIT 1);
SET @R_SUPER_ADMIN  := (SELECT id FROM roles WHERE code='SUPER_ADMIN' LIMIT 1);
SET @R_ADMIN        := (SELECT id FROM roles WHERE code='ADMIN' LIMIT 1);
SET @R_STORE_OWNER  := (SELECT id FROM roles WHERE code='STORE_OWNER' LIMIT 1);
SET @R_STORE_MGR    := (SELECT id FROM roles WHERE code='STORE_MANAGER' LIMIT 1);
SET @R_BRANCH_MGR   := (SELECT id FROM roles WHERE code='BRANCH_MANAGER' LIMIT 1);
SET @R_CASHIER      := (SELECT id FROM roles WHERE code='CASHIER' LIMIT 1);
SET @R_WAREHOUSE    := (SELECT id FROM roles WHERE code='WAREHOUSE' LIMIT 1);
SET @R_ACCOUNTANT   := (SELECT id FROM roles WHERE code='ACCOUNTANT' LIMIT 1);
SET @R_CSKH         := (SELECT id FROM roles WHERE code='CSKH_ONLINE' LIMIT 1);

INSERT IGNORE INTO user_roles (user_id, role_id) VALUES
  (@U_SYSTEM_ADMIN, @R_SYSTEM_ADMIN),
  (@U_SUPER_ADMIN,  @R_SUPER_ADMIN),
  (@U_ADMIN,        @R_ADMIN),
  (@U_STORE_OWNER,  @R_STORE_OWNER),
  (@U_STORE_MGR,    @R_STORE_MGR),
  (@U_BRANCH_MGR,   @R_BRANCH_MGR),
  (@U_CASHIER,      @R_CASHIER),
  (@U_WAREHOUSE,    @R_WAREHOUSE),
  (@U_ACCOUNTANT,   @R_ACCOUNTANT),
  (@U_CSKH,         @R_CSKH);

-- ------------------------------------------------------------
-- Optional minimal business data (small, safe defaults)
-- ------------------------------------------------------------
INSERT IGNORE INTO categories (store_id, parent_id, name, slug, description, status)
VALUES (@STORE_ID, NULL, 'Hang hoa', 'hang-hoa', 'Danh muc demo', 'ACTIVE');

SET @CAT_ID := (SELECT id FROM categories WHERE store_id=@STORE_ID AND slug='hang-hoa' LIMIT 1);

INSERT IGNORE INTO suppliers (store_id, name, contact_name, phone, email, address, tax_code, notes, status)
VALUES (@STORE_ID, 'NCC Demo', 'NCC Demo', '0900777777', 'supplier_demo@example.com', 'HCM City', NULL, 'Demo supplier', 'ACTIVE');

SET @SUP_ID := (SELECT id FROM suppliers WHERE store_id=@STORE_ID AND name='NCC Demo' LIMIT 1);

INSERT IGNORE INTO products (store_id, category_id, supplier_id, sku, name, slug, barcode, unit_name, cost_price, selling_price, status)
VALUES (@STORE_ID, @CAT_ID, @SUP_ID, 'SKU_DEMO_001', 'San pham demo', 'san-pham-demo', NULL, 'pcs', 10000, 15000, 'ACTIVE');

SET @PROD_ID := (SELECT id FROM products WHERE store_id=@STORE_ID AND sku='SKU_DEMO_001' LIMIT 1);

INSERT IGNORE INTO inventories (store_id, branch_id, product_id, variant_id, quantity, min_quantity, max_quantity, updated_at)
VALUES (@STORE_ID, @BRANCH_ID, @PROD_ID, NULL, 100, 5, 500, NOW());

