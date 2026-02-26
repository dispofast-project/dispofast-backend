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

-- Vendedor 2
INSERT INTO users (email, password_hash, full_name)
VALUES ('vendedor2@dispocol.com', crypt('Vendedor2234', gen_salt('bf')), 'Carlos Pérez')
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'VENDEDOR'
WHERE u.email = 'vendedor2@dispocol.com' ON CONFLICT DO NOTHING;

-- Vendedor 3
INSERT INTO users (email, password_hash, full_name)
VALUES ('vendedor3@dispocol.com', crypt('Vendedor3234', gen_salt('bf')), 'Laura Gómez')
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'VENDEDOR'
WHERE u.email = 'vendedor3@dispocol.com' ON CONFLICT DO NOTHING;

-- Vendedor 4
INSERT INTO users (email, password_hash, full_name)
VALUES ('vendedor4@dispocol.com', crypt('Vendedor4234', gen_salt('bf')), 'Andrés Ramírez')
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'VENDEDOR'
WHERE u.email = 'vendedor4@dispocol.com' ON CONFLICT DO NOTHING;

-- Vendedor 5
INSERT INTO users (email, password_hash, full_name)
VALUES ('vendedor5@dispocol.com', crypt('Vendedor5234', gen_salt('bf')), 'María Torres')
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'VENDEDOR'
WHERE u.email = 'vendedor5@dispocol.com' ON CONFLICT DO NOTHING;

-- Bodega 2
INSERT INTO users (email, password_hash, full_name)
VALUES ('bodega2@dispocol.com', crypt('Bodega2234', gen_salt('bf')), 'Jorge Herrera')
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'BODEGA'
WHERE u.email = 'bodega2@dispocol.com' ON CONFLICT DO NOTHING;

-- Bodega 3
INSERT INTO users (email, password_hash, full_name)
VALUES ('bodega3@dispocol.com', crypt('Bodega3234', gen_salt('bf')), 'Sofía Morales')
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'BODEGA'
WHERE u.email = 'bodega3@dispocol.com' ON CONFLICT DO NOTHING;

-- Bodega 4
INSERT INTO users (email, password_hash, full_name)
VALUES ('bodega4@dispocol.com', crypt('Bodega4234', gen_salt('bf')), 'Felipe Castro')
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'BODEGA'
WHERE u.email = 'bodega4@dispocol.com' ON CONFLICT DO NOTHING;

-- Bodega 5
INSERT INTO users (email, password_hash, full_name)
VALUES ('bodega5@dispocol.com', crypt('Bodega5234', gen_salt('bf')), 'Valentina Ruiz')
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'BODEGA'
WHERE u.email = 'bodega5@dispocol.com' ON CONFLICT DO NOTHING;

-- Vendedor 6
INSERT INTO users (email, password_hash, full_name)
VALUES ('vendedor6@dispocol.com', crypt('Vendedor6234', gen_salt('bf')), 'Diego Vargas')
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'VENDEDOR'
WHERE u.email = 'vendedor6@dispocol.com' ON CONFLICT DO NOTHING;

-- Vendedor 7
INSERT INTO users (email, password_hash, full_name)
VALUES ('vendedor7@dispocol.com', crypt('Vendedor7234', gen_salt('bf')), 'Camila Ortiz')
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'VENDEDOR'
WHERE u.email = 'vendedor7@dispocol.com' ON CONFLICT DO NOTHING;

