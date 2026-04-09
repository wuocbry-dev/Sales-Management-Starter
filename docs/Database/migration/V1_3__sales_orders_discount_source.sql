-- Support FR-28/FR-49 (apply voucher/promotion to sales orders)

ALTER TABLE sales_orders
  ADD COLUMN applied_voucher_id BIGINT NULL,
  ADD COLUMN applied_voucher_code VARCHAR(50) NULL,
  ADD COLUMN applied_promotion_id BIGINT NULL,
  ADD COLUMN applied_promotion_code VARCHAR(50) NULL,
  ADD COLUMN discount_source VARCHAR(20) NULL;

