ALTER TABLE ministries
    ADD COLUMN IF NOT EXISTS leader_id BIGINT REFERENCES users(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_ministries_leader_id ON ministries(leader_id);
