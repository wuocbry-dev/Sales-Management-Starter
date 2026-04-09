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

