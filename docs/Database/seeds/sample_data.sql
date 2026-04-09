USE sales_management;

INSERT INTO roles (name, code, description, is_system, status)
VALUES
('Super Admin', 'SUPER_ADMIN', 'Toan quyen he thong (legacy/compat)', 1, 'ACTIVE'),
('System Admin', 'SYSTEM_ADMIN', 'Quan tri he thong', 1, 'ACTIVE'),
('Store Owner', 'STORE_OWNER', 'Chu cua hang', 1, 'ACTIVE'),
('Store Manager', 'STORE_MANAGER', 'Alias tuong duong Store Owner (compat)', 1, 'ACTIVE'),
('Branch Manager', 'BRANCH_MANAGER', 'Quan ly chi nhanh', 1, 'ACTIVE'),
('Cashier', 'CASHIER', 'Thu ngan / nhan vien ban hang', 1, 'ACTIVE'),
('Warehouse', 'WAREHOUSE', 'Nhan vien kho', 1, 'ACTIVE'),
('Accountant', 'ACCOUNTANT', 'Ke toan / thu quy', 1, 'ACTIVE'),
('CSKH / Online Sales', 'CSKH_ONLINE', 'CSKH / Online Sales', 1, 'ACTIVE')
ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description), status = VALUES(status);

INSERT INTO permissions (name, code, module_name, description)
VALUES
('Dashboard - Read', 'DASHBOARD_READ', 'dashboard', 'View dashboard widgets'),
('Report - Read', 'REPORT_READ', 'report', 'View reports'),

('User - Read', 'USER_READ', 'user', 'View users'),
('User - Write', 'USER_WRITE', 'user', 'Create/update users'),
('User - Reset Password', 'USER_RESET_PASSWORD', 'user', 'Reset user passwords'),
('Role - Read', 'ROLE_READ', 'role', 'View roles'),
('Role - Write', 'ROLE_WRITE', 'role', 'Create/update roles and mappings'),
('Permission - Read', 'PERMISSION_READ', 'permission', 'View permissions catalog'),

('Store - Read', 'STORE_READ', 'store', 'View store settings'),
('Store - Write', 'STORE_WRITE', 'store', 'Update store settings'),
('Branch - Read', 'BRANCH_READ', 'branch', 'View branches'),
('Branch - Write', 'BRANCH_WRITE', 'branch', 'Create/update branches'),
('Employee - Read', 'EMPLOYEE_READ', 'employee', 'View employees'),
('Employee - Write', 'EMPLOYEE_WRITE', 'employee', 'Create/update employees'),

('Category - Read', 'CATEGORY_READ', 'category', 'View categories'),
('Category - Write', 'CATEGORY_WRITE', 'category', 'Create/update categories'),
('Product - Read', 'PRODUCT_READ', 'product', 'View products'),
('Product - Write', 'PRODUCT_WRITE', 'product', 'Create/update/disable products'),
('Product - Import', 'PRODUCT_IMPORT', 'product', 'Import products from file'),
('Product - Export', 'PRODUCT_EXPORT', 'product', 'Export products to file'),

('Supplier - Read', 'SUPPLIER_READ', 'supplier', 'View suppliers'),
('Supplier - Write', 'SUPPLIER_WRITE', 'supplier', 'Create/update suppliers'),
('Inventory - Read', 'INVENTORY_READ', 'inventory', 'View inventory'),
('Inventory - Adjust', 'INVENTORY_ADJUST', 'inventory', 'Adjust inventory quantities'),
('Purchase Order - Read', 'PURCHASE_READ', 'purchaseorder', 'View purchase orders'),
('Purchase Order - Create', 'PURCHASE_CREATE', 'purchaseorder', 'Create purchase orders'),
('Purchase Order - Receive', 'PURCHASE_RECEIVE', 'purchaseorder', 'Receive goods and post inventory'),
('Purchase Order - Cancel', 'PURCHASE_CANCEL', 'purchaseorder', 'Cancel purchase orders'),
('Purchase Order - Return', 'PURCHASE_RETURN', 'purchaseorder', 'Return goods to supplier'),

('POS Order - Read', 'POS_ORDER_READ', 'salesorder', 'View sales orders'),
('POS Order - Create', 'POS_ORDER_CREATE', 'salesorder', 'Create sales orders'),
('POS Order - Complete', 'POS_ORDER_COMPLETE', 'salesorder', 'Complete sales orders'),
('POS Order - Hold', 'POS_ORDER_HOLD', 'salesorder', 'Hold/reserve orders'),
('POS Order - Discount', 'POS_ORDER_DISCOUNT', 'salesorder', 'Apply voucher/promotion to orders'),
('POS Return - Create', 'POS_ORDER_RETURN', 'returnorder', 'Create return orders'),

('Payment - Create', 'PAYMENT_CREATE', 'payment', 'Create payments for orders'),
('Payment - Read', 'PAYMENT_READ', 'payment', 'View payments'),

('Customer - Read', 'CUSTOMER_READ', 'customer', 'View customers'),
('Customer - Write', 'CUSTOMER_WRITE', 'customer', 'Create/update customers'),
('Loyalty - Read', 'LOYALTY_READ', 'loyalty', 'View loyalty accounts/transactions'),
('Loyalty - Redeem', 'LOYALTY_REDEEM', 'loyalty', 'Redeem loyalty points'),

('Cashbook - Read', 'CASHBOOK_READ', 'cashbook', 'View cashbook entries'),
('Cashbook - Write', 'CASHBOOK_WRITE', 'cashbook', 'Create manual cashbook entries'),
('Debt - Read', 'DEBT_READ', 'finance', 'View customer/supplier debts'),

('Import Job - Read', 'IMPORT_JOB_READ', 'importjob', 'View import job status/result'),

('E-Invoice - Issue', 'EINVOICE_ISSUE', 'einvoice', 'Issue e-invoice for sales orders'),
('E-Invoice - Read', 'EINVOICE_READ', 'einvoice', 'View issued e-invoices'),

('Integration - Read', 'INTEGRATION_READ', 'integration', 'View integration channels and mappings'),
('Integration - Write', 'INTEGRATION_WRITE', 'integration', 'Create/update integration channels and mappings'),
('Integration - Sync Orders', 'INTEGRATION_SYNC_ORDERS', 'integration', 'Trigger sync orders from channel'),
('Online Order - Read', 'ONLINE_ORDER_READ', 'integration', 'View online orders'),

('Shipment - Read', 'SHIPMENT_READ', 'shipment', 'View shipments'),
('Shipment - Write', 'SHIPMENT_WRITE', 'shipment', 'Create/update shipments and statuses')
ON DUPLICATE KEY UPDATE name = VALUES(name), module_name = VALUES(module_name), description = VALUES(description);

INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
WHERE r.code IN ('SUPER_ADMIN', 'STORE_OWNER', 'STORE_MANAGER');

INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
WHERE r.code = 'SYSTEM_ADMIN'
  AND p.code IN ('USER_READ', 'USER_WRITE', 'USER_RESET_PASSWORD', 'ROLE_READ', 'ROLE_WRITE', 'PERMISSION_READ');

INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
WHERE r.code = 'BRANCH_MANAGER'
  AND p.code IN (
    'STORE_READ', 'BRANCH_READ', 'EMPLOYEE_READ',
    'CATEGORY_READ', 'CATEGORY_WRITE',
    'PRODUCT_READ', 'PRODUCT_WRITE', 'PRODUCT_IMPORT', 'PRODUCT_EXPORT',
    'SUPPLIER_READ', 'SUPPLIER_WRITE',
    'INVENTORY_READ', 'INVENTORY_ADJUST',
    'PURCHASE_READ', 'PURCHASE_CREATE', 'PURCHASE_RECEIVE', 'PURCHASE_CANCEL', 'PURCHASE_RETURN',
    'POS_ORDER_READ', 'POS_ORDER_CREATE', 'POS_ORDER_COMPLETE', 'POS_ORDER_RETURN', 'POS_ORDER_HOLD', 'POS_ORDER_DISCOUNT',
    'PAYMENT_CREATE', 'PAYMENT_READ',
    'CUSTOMER_READ', 'CUSTOMER_WRITE',
    'LOYALTY_READ', 'LOYALTY_REDEEM',
    'CASHBOOK_READ', 'CASHBOOK_WRITE',
    'REPORT_READ', 'DASHBOARD_READ',
    'INTEGRATION_READ', 'INTEGRATION_WRITE', 'INTEGRATION_SYNC_ORDERS', 'ONLINE_ORDER_READ',
    'SHIPMENT_READ', 'SHIPMENT_WRITE'
  );

INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
WHERE r.code = 'CASHIER'
  AND p.code IN ('DASHBOARD_READ', 'POS_ORDER_READ', 'POS_ORDER_CREATE', 'POS_ORDER_COMPLETE', 'POS_ORDER_HOLD', 'POS_ORDER_DISCOUNT', 'PAYMENT_CREATE', 'PAYMENT_READ', 'CUSTOMER_READ');

INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
WHERE r.code = 'WAREHOUSE'
  AND p.code IN ('PRODUCT_READ', 'INVENTORY_READ', 'INVENTORY_ADJUST', 'SUPPLIER_READ', 'PURCHASE_READ', 'PURCHASE_CREATE', 'PURCHASE_RECEIVE', 'PURCHASE_RETURN');

INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
WHERE r.code = 'ACCOUNTANT'
  AND p.code IN ('CASHBOOK_READ', 'CASHBOOK_WRITE', 'DEBT_READ', 'REPORT_READ', 'PAYMENT_READ');

INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
WHERE r.code = 'CSKH_ONLINE'
  AND p.code IN ('CUSTOMER_READ', 'CUSTOMER_WRITE', 'LOYALTY_READ', 'LOYALTY_REDEEM', 'ONLINE_ORDER_READ', 'INTEGRATION_READ');

INSERT INTO users (full_name, username, email, phone, password_hash, status)
VALUES
('Nguyễn Văn Admin', 'admin', 'admin@gmail.com', '0900000001', '$2b$10$AhvOnu3txsorbbY204939e.XKWta.9bAr/lRYPus5pvXbWvGCyf7m', 'ACTIVE'),
('Trần Thị Manager', 'manager01', 'manager@gmail.com', '0900000002', '$2b$10$AhvOnu3txsorbbY204939e.XKWta.9bAr/lRYPus5pvXbWvGCyf7m', 'ACTIVE'),
('Lê Văn Cashier', 'cashier01', 'cashier@gmail.com', '0900000003', '$2b$10$AhvOnu3txsorbbY204939e.XKWta.9bAr/lRYPus5pvXbWvGCyf7m', 'ACTIVE'),
('Phạm Thị Warehouse', 'warehouse01', 'warehouse@gmail.com', '0900000004', '$2b$10$AhvOnu3txsorbbY204939e.XKWta.9bAr/lRYPus5pvXbWvGCyf7m', 'ACTIVE'),
('Võ Thị Accountant', 'accountant01', 'accountant@gmail.com', '0900000005', '$2b$10$AhvOnu3txsorbbY204939e.XKWta.9bAr/lRYPus5pvXbWvGCyf7m', 'ACTIVE'),
('Nguyễn Thị CSKH', 'cskh01', 'cskh@gmail.com', '0900000006', '$2b$10$AhvOnu3txsorbbY204939e.XKWta.9bAr/lRYPus5pvXbWvGCyf7m', 'ACTIVE')
ON DUPLICATE KEY UPDATE full_name = VALUES(full_name), status = VALUES(status);

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.code = 'SUPER_ADMIN' WHERE u.username = 'admin';

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.code = 'STORE_MANAGER' WHERE u.username = 'manager01';

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.code = 'CASHIER' WHERE u.username = 'cashier01';

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.code = 'WAREHOUSE' WHERE u.username = 'warehouse01';

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.code = 'ACCOUNTANT' WHERE u.username = 'accountant01';

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.code = 'CSKH_ONLINE' WHERE u.username = 'cskh01';

INSERT INTO stores (name, code, business_type, phone, email, address, tax_code, status, owner_user_id)
SELECT 'Demo Fashion Store', 'STORE_DEMO_001', 'RETAIL_FASHION', '0901234567', 'store@gmail.com', '123 Le Loi, Ninh Kieu, Can Tho', '1800123456', 'ACTIVE', u.id
FROM users u WHERE u.username = 'admin'
ON DUPLICATE KEY UPDATE name = VALUES(name), business_type = VALUES(business_type), status = VALUES(status);

INSERT INTO branches (store_id, name, code, phone, email, address, is_default, status)
SELECT s.id, 'Chi nhanh Trung tam', 'BRANCH_CT_001', '0901234568', 'branch@gmail.com', '123 Le Loi, Ninh Kieu, Can Tho', 1, 'ACTIVE'
FROM stores s WHERE s.code = 'STORE_DEMO_001'
ON DUPLICATE KEY UPDATE name = VALUES(name), is_default = VALUES(is_default), status = VALUES(status);

INSERT INTO employees (store_id, branch_id, user_id, employee_code, full_name, phone, email, position_name, hire_date, salary, status)
SELECT s.id, b.id, u.id, 'EMP0001', 'Tran Thi Manager', '0900000002', 'manager@gmail.com', 'Store Manager', '2025-01-10', 15000000, 'ACTIVE'
FROM stores s
JOIN branches b ON b.store_id = s.id AND b.code = 'BRANCH_CT_001'
JOIN users u ON u.username = 'manager01'
WHERE s.code = 'STORE_DEMO_001'
ON DUPLICATE KEY UPDATE full_name = VALUES(full_name), position_name = VALUES(position_name), salary = VALUES(salary), status = VALUES(status);

INSERT INTO employees (store_id, branch_id, user_id, employee_code, full_name, phone, email, position_name, hire_date, salary, status)
SELECT s.id, b.id, u.id, 'EMP0002', 'Le Van Cashier', '0900000003', 'cashier@gmail.com', 'Cashier', '2025-02-01', 9000000, 'ACTIVE'
FROM stores s
JOIN branches b ON b.store_id = s.id AND b.code = 'BRANCH_CT_001'
JOIN users u ON u.username = 'cashier01'
WHERE s.code = 'STORE_DEMO_001'
ON DUPLICATE KEY UPDATE full_name = VALUES(full_name), position_name = VALUES(position_name), salary = VALUES(salary), status = VALUES(status);

INSERT INTO categories (store_id, parent_id, name, slug, description, status)
SELECT s.id, NULL, 'Thoi trang nu', 'thoi-trang-nu', 'Danh muc thoi trang nu', 'ACTIVE'
FROM stores s WHERE s.code = 'STORE_DEMO_001';

INSERT INTO categories (store_id, parent_id, name, slug, description, status)
SELECT s.id, NULL, 'Phu kien', 'phu-kien', 'Danh muc phu kien', 'ACTIVE'
FROM stores s WHERE s.code = 'STORE_DEMO_001';

INSERT INTO suppliers (store_id, name, contact_name, phone, email, address, tax_code, notes, status)
SELECT s.id, 'Cong ty TNHH Nguon Hang A', 'Pham Thi Hoa', '0911000001', 'supplierA@example.com', 'Kho 1, Binh Tan, TP.HCM', '0312345678', 'Nha cung cap mac dinh', 'ACTIVE'
FROM stores s WHERE s.code = 'STORE_DEMO_001';

INSERT INTO customers (store_id, customer_code, full_name, phone, email, gender, date_of_birth, address, total_points, total_spent, last_order_at, status)
SELECT s.id, 'CUS0001', 'Nguyen Thi Lan', '0988000001', 'lan@example.com', 'FEMALE', '1999-04-12', 'Can Tho', 120, 1350000, NOW(), 'ACTIVE'
FROM stores s WHERE s.code = 'STORE_DEMO_001'
ON DUPLICATE KEY UPDATE full_name = VALUES(full_name), total_points = VALUES(total_points), total_spent = VALUES(total_spent);

INSERT INTO customers (store_id, customer_code, full_name, phone, email, gender, date_of_birth, address, total_points, total_spent, last_order_at, status)
SELECT s.id, 'CUS0002', 'Pham Van Minh', '0988000002', 'minh@example.com', 'MALE', '1997-08-20', 'Vinh Long', 40, 520000, NOW(), 'ACTIVE'
FROM stores s WHERE s.code = 'STORE_DEMO_001'
ON DUPLICATE KEY UPDATE full_name = VALUES(full_name), total_points = VALUES(total_points), total_spent = VALUES(total_spent);

INSERT INTO products (store_id, category_id, supplier_id, sku, name, slug, barcode, unit_name, description, cost_price, selling_price, track_inventory, status)
SELECT s.id, c.id, sp.id, 'SKU-TSHIRT-001', 'Ao thun basic nu', 'ao-thun-basic-nu', '893000000001', 'cai', 'Ao thun basic co tron', 95000, 149000, 1, 'ACTIVE'
FROM stores s
JOIN categories c ON c.store_id = s.id AND c.slug = 'thoi-trang-nu'
JOIN suppliers sp ON sp.store_id = s.id AND sp.name = 'Cong ty TNHH Nguon Hang A'
WHERE s.code = 'STORE_DEMO_001'
ON DUPLICATE KEY UPDATE name = VALUES(name), cost_price = VALUES(cost_price), selling_price = VALUES(selling_price);

INSERT INTO products (store_id, category_id, supplier_id, sku, name, slug, barcode, unit_name, description, cost_price, selling_price, track_inventory, status)
SELECT s.id, c.id, sp.id, 'SKU-BAG-001', 'Tui tote canvas', 'tui-tote-canvas', '893000000002', 'cai', 'Tui tote canvas unisex', 70000, 119000, 1, 'ACTIVE'
FROM stores s
JOIN categories c ON c.store_id = s.id AND c.slug = 'phu-kien'
JOIN suppliers sp ON sp.store_id = s.id AND sp.name = 'Cong ty TNHH Nguon Hang A'
WHERE s.code = 'STORE_DEMO_001'
ON DUPLICATE KEY UPDATE name = VALUES(name), cost_price = VALUES(cost_price), selling_price = VALUES(selling_price);

INSERT INTO product_variants (product_id, sku, barcode, variant_name, option1_name, option1_value, option2_name, option2_value, cost_price, selling_price, status)
SELECT p.id, 'SKU-TSHIRT-001-WHITE-M', '893000000011', 'White / M', 'Color', 'White', 'Size', 'M', 95000, 149000, 'ACTIVE'
FROM products p WHERE p.sku = 'SKU-TSHIRT-001'
ON DUPLICATE KEY UPDATE variant_name = VALUES(variant_name), selling_price = VALUES(selling_price);

INSERT INTO product_variants (product_id, sku, barcode, variant_name, option1_name, option1_value, option2_name, option2_value, cost_price, selling_price, status)
SELECT p.id, 'SKU-TSHIRT-001-BLACK-L', '893000000012', 'Black / L', 'Color', 'Black', 'Size', 'L', 95000, 149000, 'ACTIVE'
FROM products p WHERE p.sku = 'SKU-TSHIRT-001'
ON DUPLICATE KEY UPDATE variant_name = VALUES(variant_name), selling_price = VALUES(selling_price);

INSERT INTO inventories (store_id, branch_id, product_id, variant_id, quantity, reserved_quantity, min_quantity, max_quantity)
SELECT s.id, b.id, p.id, pv.id, 50, 2, 5, 200
FROM stores s
JOIN branches b ON b.store_id = s.id AND b.code = 'BRANCH_CT_001'
JOIN products p ON p.store_id = s.id AND p.sku = 'SKU-TSHIRT-001'
JOIN product_variants pv ON pv.product_id = p.id AND pv.sku = 'SKU-TSHIRT-001-WHITE-M'
WHERE s.code = 'STORE_DEMO_001';

INSERT INTO inventories (store_id, branch_id, product_id, variant_id, quantity, reserved_quantity, min_quantity, max_quantity)
SELECT s.id, b.id, p.id, pv.id, 35, 1, 5, 200
FROM stores s
JOIN branches b ON b.store_id = s.id AND b.code = 'BRANCH_CT_001'
JOIN products p ON p.store_id = s.id AND p.sku = 'SKU-TSHIRT-001'
JOIN product_variants pv ON pv.product_id = p.id AND pv.sku = 'SKU-TSHIRT-001-BLACK-L'
WHERE s.code = 'STORE_DEMO_001';

INSERT INTO inventories (store_id, branch_id, product_id, variant_id, quantity, reserved_quantity, min_quantity, max_quantity)
SELECT s.id, b.id, p.id, NULL, 80, 0, 10, 300
FROM stores s
JOIN branches b ON b.store_id = s.id AND b.code = 'BRANCH_CT_001'
JOIN products p ON p.store_id = s.id AND p.sku = 'SKU-BAG-001'
WHERE s.code = 'STORE_DEMO_001';

INSERT INTO sales_orders (store_id, branch_id, customer_id, order_number, order_source, status, subtotal, discount_amount, tax_amount, shipping_fee, total_amount, paid_amount, notes, sold_by, ordered_at)
SELECT s.id, b.id, c.id, 'SO-20260407-0001', 'POS', 'COMPLETED', 417000, 20000, 0, 0, 397000, 397000, 'Don hang demo', u.id, NOW()
FROM stores s
JOIN branches b ON b.store_id = s.id AND b.code = 'BRANCH_CT_001'
JOIN customers c ON c.store_id = s.id AND c.customer_code = 'CUS0001'
JOIN users u ON u.username = 'cashier01'
WHERE s.code = 'STORE_DEMO_001'
ON DUPLICATE KEY UPDATE total_amount = VALUES(total_amount), paid_amount = VALUES(paid_amount), status = VALUES(status);

INSERT INTO sales_order_items (sales_order_id, product_id, variant_id, product_name, sku, unit_price, quantity, discount_amount, line_total)
SELECT so.id, p.id, pv.id, 'Ao thun basic nu', 'SKU-TSHIRT-001-WHITE-M', 149000, 2, 10000, 288000
FROM sales_orders so
JOIN products p ON p.sku = 'SKU-TSHIRT-001'
JOIN product_variants pv ON pv.product_id = p.id AND pv.sku = 'SKU-TSHIRT-001-WHITE-M'
WHERE so.order_number = 'SO-20260407-0001';

INSERT INTO sales_order_items (sales_order_id, product_id, variant_id, product_name, sku, unit_price, quantity, discount_amount, line_total)
SELECT so.id, p.id, NULL, 'Tui tote canvas', 'SKU-BAG-001', 119000, 1, 10000, 109000
FROM sales_orders so
JOIN products p ON p.sku = 'SKU-BAG-001'
WHERE so.order_number = 'SO-20260407-0001';

INSERT INTO payments (store_id, branch_id, sales_order_id, payment_code, payment_method, status, amount, paid_at, transaction_ref, notes, created_by)
SELECT so.store_id, so.branch_id, so.id, 'PAY-20260407-0001', 'CASH', 'PAID', 397000, NOW(), NULL, 'Thanh toan tai quay', u.id
FROM sales_orders so
JOIN users u ON u.username = 'cashier01'
WHERE so.order_number = 'SO-20260407-0001'
ON DUPLICATE KEY UPDATE amount = VALUES(amount), status = VALUES(status);

INSERT INTO cashbook_entries (store_id, branch_id, entry_type, category, reference_type, reference_id, amount, description, occurred_at, created_by)
SELECT so.store_id, so.branch_id, 'INCOME', 'BAN_HANG', 'SALES_ORDER', so.id, 397000, 'Thu tien don hang SO-20260407-0001', NOW(), u.id
FROM sales_orders so
JOIN users u ON u.username = 'cashier01'
WHERE so.order_number = 'SO-20260407-0001';

INSERT INTO promotions (store_id, name, code, promotion_type, value_type, value_amount, min_order_amount, max_discount_amount, start_at, end_at, status)
SELECT s.id, 'Khuyen mai khai truong', 'PROMO_OPENING', 'ORDER', 'AMOUNT', 20000, 200000, 20000, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 'ACTIVE'
FROM stores s WHERE s.code = 'STORE_DEMO_001'
ON DUPLICATE KEY UPDATE name = VALUES(name), value_amount = VALUES(value_amount), end_at = VALUES(end_at), status = VALUES(status);

INSERT INTO vouchers (store_id, promotion_id, code, discount_type, discount_value, min_order_amount, max_discount_amount, usage_limit, used_count, start_at, end_at, status)
SELECT s.id, p.id, 'WELCOME20K', 'AMOUNT', 20000, 200000, 20000, 100, 1, NOW(), DATE_ADD(NOW(), INTERVAL 60 DAY), 'ACTIVE'
FROM stores s
JOIN promotions p ON p.store_id = s.id AND p.code = 'PROMO_OPENING'
WHERE s.code = 'STORE_DEMO_001'
ON DUPLICATE KEY UPDATE discount_value = VALUES(discount_value), used_count = VALUES(used_count), status = VALUES(status);

INSERT INTO loyalty_accounts (store_id, customer_id, current_points, lifetime_points, tier_name)
SELECT s.id, c.id, 120, 120, 'SILVER'
FROM stores s
JOIN customers c ON c.store_id = s.id AND c.customer_code = 'CUS0001'
WHERE s.code = 'STORE_DEMO_001'
ON DUPLICATE KEY UPDATE current_points = VALUES(current_points), lifetime_points = VALUES(lifetime_points), tier_name = VALUES(tier_name);

INSERT INTO loyalty_transactions (loyalty_account_id, reference_type, reference_id, points_change, description)
SELECT la.id, 'SALES_ORDER', so.id, 39, 'Cong diem cho don SO-20260407-0001'
FROM loyalty_accounts la
JOIN customers c ON c.id = la.customer_id AND c.customer_code = 'CUS0001'
JOIN sales_orders so ON so.customer_id = c.id AND so.order_number = 'SO-20260407-0001';

INSERT INTO settings (store_id, setting_key, setting_value, description)
SELECT s.id, 'currency', 'VND', 'Don vi tien te mac dinh'
FROM stores s WHERE s.code = 'STORE_DEMO_001'
ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value), description = VALUES(description);

INSERT INTO settings (store_id, setting_key, setting_value, description)
SELECT s.id, 'timezone', 'Asia/Ho_Chi_Minh', 'Mui gio he thong'
FROM stores s WHERE s.code = 'STORE_DEMO_001'
ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value), description = VALUES(description);

INSERT INTO notifications (store_id, user_id, title, message, type, is_read)
SELECT s.id, u.id, 'Chao mung', 'Tai khoan cua ban da duoc khoi tao thanh cong.', 'SYSTEM', 0
FROM stores s
JOIN users u ON u.username = 'admin'
WHERE s.code = 'STORE_DEMO_001';

INSERT INTO audit_logs (store_id, branch_id, actor_user_id, actor_username, module, action, entity_type, entity_id, message, ip, user_agent)
SELECT s.id, b.id, u.id, u.username, 'salesorder', 'CREATE', 'SALES_ORDER', so.id,
       CONCAT('Seed created order ', so.order_number, ', total=', so.total_amount),
       '127.0.0.1', 'Starter Seed Script'
FROM stores s
JOIN branches b ON b.store_id = s.id AND b.code = 'BRANCH_CT_001'
JOIN users u ON u.username = 'cashier01'
JOIN sales_orders so ON so.store_id = s.id AND so.order_number = 'SO-20260407-0001'
WHERE s.code = 'STORE_DEMO_001';

-- =========================
-- Phase 3 foundations sample data
-- =========================

INSERT INTO import_jobs (store_id, type, status, original_filename, content_type, created_by, total_rows, success_rows, failed_rows, error_message, result_file_path, started_at, finished_at)
SELECT s.id, 'PRODUCT_IMPORT', 'SUCCEEDED', 'products-demo.csv', 'text/csv', u.id, 2, 2, 0, NULL, NULL, NOW(), NOW()
FROM stores s
JOIN users u ON u.username = 'admin'
WHERE s.code = 'STORE_DEMO_001';

INSERT INTO e_invoices (store_id, sales_order_id, status, provider_name, provider_invoice_id, invoice_number,
                        buyer_name, buyer_tax_code, buyer_address, buyer_email,
                        subtotal, tax_amount, total_amount, issued_at, error_message, created_by)
SELECT so.store_id, so.id, 'ISSUED', 'MOCK', CONCAT('prov_', so.id), CONCAT('INV-', so.id),
       c.full_name, NULL, c.address, c.email,
       so.subtotal, so.tax_amount, so.total_amount, NOW(), NULL, u.id
FROM sales_orders so
JOIN customers c ON c.id = so.customer_id
JOIN users u ON u.username = 'admin'
WHERE so.order_number = 'SO-20260407-0001'
ON DUPLICATE KEY UPDATE status = VALUES(status), invoice_number = VALUES(invoice_number), issued_at = VALUES(issued_at);

INSERT INTO integration_channels (store_id, channel_type, channel_name, channel_code, status, config_json)
SELECT s.id, 'SHOPEE', 'Shopee Demo', 'SHOPEE_DEMO', 'ACTIVE', JSON_OBJECT('mode', 'mock')
FROM stores s
WHERE s.code = 'STORE_DEMO_001'
ON DUPLICATE KEY UPDATE channel_name = VALUES(channel_name), status = VALUES(status), config_json = VALUES(config_json);

INSERT INTO online_orders (store_id, channel_id, external_order_id, status, external_order_number,
                           buyer_name, buyer_phone, shipping_address,
                           subtotal, discount_amount, shipping_fee, total_amount,
                           items_json, raw_payload, synced_at)
SELECT s.id, ch.id, 'EXT-ORDER-001', 'NEW', 'SPX-0001',
       'Online Buyer A', '0987000001', 'Can Tho',
       268000, 0, 15000, 283000,
       JSON_ARRAY(
         JSON_OBJECT('sku','SKU-BAG-001','qty',1,'price',119000),
         JSON_OBJECT('sku','SKU-TSHIRT-001-WHITE-M','qty',1,'price',149000)
       ),
       JSON_OBJECT('source','seed'),
       NOW()
FROM stores s
JOIN integration_channels ch ON ch.store_id = s.id AND ch.channel_code = 'SHOPEE_DEMO'
WHERE s.code = 'STORE_DEMO_001'
ON DUPLICATE KEY UPDATE status = VALUES(status), synced_at = VALUES(synced_at), total_amount = VALUES(total_amount);