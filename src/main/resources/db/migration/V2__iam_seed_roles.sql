INSERT INTO roles (name) VALUES
    ('ADMIN'),
    ('VENDEDOR'),
    ('BODEGA')
ON CONFLICT (name) DO NOTHING;