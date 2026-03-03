-- ============================================================
-- V11: IAM – Permissions seeding and role_permissions table
-- ============================================================

-- 1. Role-permissions join table
--    Allows assigning a set of default permissions to a role.
--    Individual overrides can still be added via user_permissions.
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id       UUID NOT NULL,
    permission_id UUID NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_rp_role       FOREIGN KEY (role_id)       REFERENCES roles(id)       ON DELETE CASCADE,
    CONSTRAINT fk_rp_permission FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_role_permissions_role_id       ON role_permissions(role_id);
CREATE INDEX IF NOT EXISTS idx_role_permissions_permission_id ON role_permissions(permission_id);

-- 2. Seed all system permissions
--    Convention: {MODULE}_{ACTION}
--    Actions: VIEW | CREATE | EDIT | DELETE
--    Non-ADMIN roles can only receive VIEW, CREATE, EDIT –
--    DELETE permissions are reserved exclusively for the ADMIN role.
-- ------------------------------------------------------------
INSERT INTO permissions (name) VALUES
    -- IAM: gestión de usuarios, roles y permisos
    ('IAM_VIEW'),
    ('IAM_CREATE'),
    ('IAM_EDIT'),
    ('IAM_DELETE'),

    -- Clientes
    ('CUSTOMERS_VIEW'),
    ('CUSTOMERS_CREATE'),
    ('CUSTOMERS_EDIT'),
    ('CUSTOMERS_DELETE'),

    -- Inventario
    ('INVENTORY_VIEW'),
    ('INVENTORY_CREATE'),
    ('INVENTORY_EDIT'),
    ('INVENTORY_DELETE'),

    -- Órdenes de compra
    ('PURCHASE_ORDERS_VIEW'),
    ('PURCHASE_ORDERS_CREATE'),
    ('PURCHASE_ORDERS_EDIT'),
    ('PURCHASE_ORDERS_DELETE'),

    -- Lista de precios
    ('PRICE_LISTS_VIEW'),
    ('PRICE_LISTS_CREATE'),
    ('PRICE_LISTS_EDIT'),
    ('PRICE_LISTS_DELETE'),

    -- Cotizaciones
    ('QUOTES_VIEW'),
    ('QUOTES_CREATE'),
    ('QUOTES_EDIT'),
    ('QUOTES_DELETE'),

    -- Cartera (cuentas)
    ('ACCOUNTS_VIEW'),
    ('ACCOUNTS_CREATE'),
    ('ACCOUNTS_EDIT'),
    ('ACCOUNTS_DELETE'),

    -- Prospectos
    ('PROSPECTS_VIEW'),
    ('PROSPECTS_CREATE'),
    ('PROSPECTS_EDIT'),
    ('PROSPECTS_DELETE')

ON CONFLICT (name) DO NOTHING;

-- 3. ADMIN: acceso total a todos los módulos y acciones
-- ------------------------------------------------------------
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM   roles r
CROSS  JOIN permissions p
WHERE  r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

-- 4. VENDEDOR: permisos base – ver, crear y editar en sus módulos;
--    sin acceso a eliminación ni a módulos de gestión interna (IAM, Inventario).
--    Permisos adicionales pueden asignarse por usuario mediante user_permissions.
-- ------------------------------------------------------------
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM   roles r
JOIN   permissions p ON p.name IN (
    -- Clientes
    'CUSTOMERS_VIEW',        'CUSTOMERS_CREATE',        'CUSTOMERS_EDIT',
    -- Órdenes de compra
    'PURCHASE_ORDERS_VIEW',  'PURCHASE_ORDERS_CREATE',  'PURCHASE_ORDERS_EDIT',
    -- Lista de precios
    'PRICE_LISTS_VIEW',      'PRICE_LISTS_CREATE',      'PRICE_LISTS_EDIT',
    -- Cotizaciones
    'QUOTES_VIEW',           'QUOTES_CREATE',           'QUOTES_EDIT',
    -- Cartera
    'ACCOUNTS_VIEW',         'ACCOUNTS_CREATE',         'ACCOUNTS_EDIT',
    -- Prospectos
    'PROSPECTS_VIEW',        'PROSPECTS_CREATE',        'PROSPECTS_EDIT'
)
WHERE  r.name = 'VENDEDOR'
ON CONFLICT DO NOTHING;

-- 5. BODEGA: sin permisos base predefinidos.
--    Los permisos se asignan individualmente a cada usuario mediante user_permissions
--    según las necesidades operativas (típicamente módulos de Inventario).
-- ------------------------------------------------------------
