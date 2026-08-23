ALTER TABLE users ADD COLUMN cpf VARCHAR(11);
ALTER TABLE users ADD COLUMN emergency_contact_name VARCHAR(255);
ALTER TABLE users ADD COLUMN emergency_contact_phone VARCHAR(30);
CREATE UNIQUE INDEX uk_users_cpf ON users(cpf) WHERE cpf IS NOT NULL;
