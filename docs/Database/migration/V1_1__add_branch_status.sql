-- Add branch status to support FR-04 (lock/unlock branch)
-- Values: ACTIVE, INACTIVE

ALTER TABLE branches
  ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

-- Backfill safety (in case of non-default behavior)
UPDATE branches SET status = 'ACTIVE' WHERE status IS NULL OR status = '';

