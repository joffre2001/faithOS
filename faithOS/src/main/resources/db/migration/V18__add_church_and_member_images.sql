ALTER TABLE churches ADD COLUMN logo_stored_name VARCHAR(100);
ALTER TABLE churches ADD COLUMN logo_content_type VARCHAR(50);
ALTER TABLE users ADD COLUMN profile_picture_stored_name VARCHAR(100);
ALTER TABLE users ADD COLUMN profile_picture_content_type VARCHAR(50);
