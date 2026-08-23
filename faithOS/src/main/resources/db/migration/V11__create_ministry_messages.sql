CREATE TABLE ministry_messages (
    id BIGSERIAL PRIMARY KEY,
    ministry_id BIGINT NOT NULL REFERENCES ministries(id) ON DELETE CASCADE,
    sender_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    message VARCHAR(2000),
    attachment_name VARCHAR(255),
    attachment_type VARCHAR(255),
    attachment_size BIGINT,
    attachment_data BYTEA,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ministry_message_has_content CHECK (message IS NOT NULL OR attachment_data IS NOT NULL)
);
CREATE INDEX idx_ministry_messages_ministry_created ON ministry_messages(ministry_id, created_at);
