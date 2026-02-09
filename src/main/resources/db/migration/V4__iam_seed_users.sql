-- Seed additional application users
-- Requires pgcrypto (enabled in V1__iam_base.sql)

-- Vendedor user
INSERT INTO users (email, password_hash, full_name)
VALUES (
	'vendedor@dispocol.com',
	crypt('Vendedor1234', gen_salt('bf')),
	'Vendedor Dispofast'
)
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'VENDEDOR'
WHERE u.email = 'vendedor@dispocol.com'
ON CONFLICT DO NOTHING;

-- Bodega user
INSERT INTO users (email, password_hash, full_name)
VALUES (
	'bodega@dispocol.com',
	crypt('Bodega1234', gen_salt('bf')),
	'Bodega Dispofast'
)
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'BODEGA'
WHERE u.email = 'bodega@dispocol.com'
ON CONFLICT DO NOTHING;

