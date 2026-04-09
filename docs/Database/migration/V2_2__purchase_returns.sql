-- FR-21 Purchase returns (return goods to supplier)

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

CREATE INDEX IF NOT EXISTS idx_purchase_returns_store_po ON purchase_returns(store_id, purchase_order_id);
CREATE INDEX IF NOT EXISTS idx_purchase_return_items_pr ON purchase_return_items(purchase_return_id);
CREATE INDEX IF NOT EXISTS idx_purchase_return_items_poi ON purchase_return_items(purchase_order_item_id);

