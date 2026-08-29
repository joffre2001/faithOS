ALTER TABLE churches ADD COLUMN logo_data BYTEA;
ALTER TABLE users ADD COLUMN profile_picture_data BYTEA;
ALTER TABLE churches DROP COLUMN logo_stored_name;
ALTER TABLE users DROP COLUMN profile_picture_stored_name;
