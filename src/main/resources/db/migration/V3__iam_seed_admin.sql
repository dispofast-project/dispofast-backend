INSERT INTO users(email, password_hash, full_name) VALUES
    ('admin@dispocol.com', 
    '$2b$12$2PqOmUhpMp8GgkFoWLQ3z.dCOLLBp7ecRqj4nqMXVW/wMf74w9i/e',  --Admin1234
    'Administrador Dispofast')
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'ADMIN'
WHERE u.email = 'admin@dispocol.com';