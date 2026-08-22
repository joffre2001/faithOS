CREATE TABLE IF NOT EXISTS ministry_members (
    ministry_id BIGINT NOT NULL REFERENCES ministries(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY (ministry_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_ministry_members_user_id ON ministry_members(user_id);
