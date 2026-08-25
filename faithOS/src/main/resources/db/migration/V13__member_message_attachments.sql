ALTER TABLE member_messages ADD COLUMN attachment_name VARCHAR(255);
ALTER TABLE member_messages ADD COLUMN attachment_type VARCHAR(255);
ALTER TABLE member_messages ADD COLUMN attachment_size BIGINT;
ALTER TABLE member_messages ADD COLUMN attachment_data BYTEA;
