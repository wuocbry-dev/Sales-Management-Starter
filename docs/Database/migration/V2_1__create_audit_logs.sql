-- FR-08 Audit logs (idempotent-ish for MySQL)

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

CREATE INDEX IF NOT EXISTS idx_audit_logs_store_created ON audit_logs(store_id, created_at);
CREATE INDEX IF NOT EXISTS idx_audit_logs_store_module ON audit_logs(store_id, module);
CREATE INDEX IF NOT EXISTS idx_audit_logs_store_actor ON audit_logs(store_id, actor_user_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_store_branch ON audit_logs(store_id, branch_id);

