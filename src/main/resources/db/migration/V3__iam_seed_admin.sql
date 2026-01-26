INSERT INTO users(email, password_hash, full_name) VALUES
    ('admin@dispocol.com', "[INSERT HASHED PASSWORD]", "Administrador Dispofast")
ON CONFLICT (email) DO NOTHING;
