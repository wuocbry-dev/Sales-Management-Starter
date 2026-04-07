USE sales_management;

INSERT INTO roles (name, code, description, is_system, status)
VALUES
('Super Admin', 'SUPER_ADMIN', 'Toan quyen he thong', 1, 'ACTIVE'),
('Store Manager', 'STORE_MANAGER', 'Quan ly cua hang', 1, 'ACTIVE'),
('Cashier', 'CASHIER', 'Thu ngan / nhan vien ban hang', 1, 'ACTIVE')
ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description), status = VALUES(status);

INSERT INTO permissions (name, code, module_name, description)
VALUES
('View dashboard', 'dashboard:view', 'dashboard', 'Xem dashboard'),
('Manage products', 'product:manage', 'product', 'Quan ly san pham'),
('Manage inventory', 'inventory:manage', 'inventory', 'Quan ly ton kho'),
('Create sales order', 'sales-order:create', 'sales-order', 'Tao don ban'),
('View reports', 'report:view', 'report', 'Xem bao cao'),
('Manage users', 'user:manage', 'user', 'Quan ly nguoi dung')
ON DUPLICATE KEY UPDATE name = VALUES(name), module_name = VALUES(module_name), description = VALUES(description);

INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
WHERE r.code = 'SUPER_ADMIN';

INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
WHERE r.code = 'STORE_MANAGER'
  AND p.code IN ('dashboard:view', 'product:manage', 'inventory:manage', 'sales-order:create', 'report:view');

INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
WHERE r.code = 'CASHIER'
  AND p.code IN ('dashboard:view', 'sales-order:create');

INSERT INTO users (full_name, username, email, phone, password_hash, status)
VALUES
('Nguyen Van Admin', 'admin', 'admin@sales.local', '0900000001', '$2a$10$examplehashedpasswordforstarteronly', 'ACTIVE'),
('Tran Thi Manager', 'manager01', 'manager@sales.local', '0900000002', '$2a$10$examplehashedpasswordforstarteronly', 'ACTIVE'),
('Le Van Cashier', 'cashier01', 'cashier@sales.local', '0900000003', '$2a$10$examplehashedpasswordforstarteronly', 'ACTIVE')
ON DUPLICATE KEY UPDATE full_name = VALUES(full_name), status = VALUES(status);

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.code = 'SUPER_ADMIN' WHERE u.username = 'admin';

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.code = 'STORE_MANAGER' WHERE u.username = 'manager01';

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.code = 'CASHIER' WHERE u.username = 'cashier01';

INSERT INTO stores (name, code, business_type, phone, email, address, tax_code, status, owner_user_id)
SELECT 'Demo Fashion Store', 'STORE_DEMO_001', 'RETAIL_FASHION', '0901234567', 'store@sales.local', '123 Le Loi, Ninh Kieu, Can Tho', '1800123456', 'ACTIVE', u.id
FROM users u WHERE u.username = 'admin'
ON DUPLICATE KEY UPDATE name = VALUES(name), business_type = VALUES(business_type), status = VALUES(status);

INSERT INTO branches (store_id, name, code, phone, email, address, is_default, status)
SELECT s.id, 'Chi nhanh Trung tam', 'BRANCH_CT_001', '0901234568', 'branch@sales.local', '123 Le Loi, Ninh Kieu, Can Tho', 1, 'ACTIVE'
FROM stores s WHERE s.code = 'STORE_DEMO_001'
ON DUPLICATE KEY UPDATE name = VALUES(name), is_default = VALUES(is_default), status = VALUES(status);

INSERT INTO employees (store_id, branch_id, user_id, employee_code, full_name, phone, email, position_name, hire_date, salary, status)
SELECT s.id, b.id, u.id, 'EMP0001', 'Tran Thi Manager', '0900000002', 'manager@sales.local', 'Store Manager', '2025-01-10', 15000000, 'ACTIVE'
FROM stores s
JOIN branches b ON b.store_id = s.id AND b.code = 'BRANCH_CT_001'
JOIN users u ON u.username = 'manager01'
WHERE s.code = 'STORE_DEMO_001'
ON DUPLICATE KEY UPDATE full_name = VALUES(full_name), position_name = VALUES(position_name), salary = VALUES(salary), status = VALUES(status);

INSERT INTO employees (store_id, branch_id, user_id, employee_code, full_name, phone, email, position_name, hire_date, salary, status)
SELECT s.id, b.id, u.id, 'EMP0002', 'Le Van Cashier', '0900000003', 'cashier@sales.local', 'Cashier', '2025-02-01', 9000000, 'ACTIVE'
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

INSERT INTO audit_logs (store_id, user_id, action_name, module_name, reference_type, reference_id, ip_address, user_agent, details_json)
SELECT s.id, u.id, 'CREATE_ORDER', 'sales-order', 'SALES_ORDER', so.id, '127.0.0.1', 'Starter Seed Script', JSON_OBJECT('order_number', so.order_number, 'amount', so.total_amount)
FROM stores s
JOIN users u ON u.username = 'cashier01'
JOIN sales_orders so ON so.store_id = s.id AND so.order_number = 'SO-20260407-0001'
WHERE s.code = 'STORE_DEMO_001';
