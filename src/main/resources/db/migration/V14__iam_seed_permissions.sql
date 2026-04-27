-- ============================================================
-- V11: IAM – Permissions seeding and role_permissions table
-- ============================================================

-- 1. Seed all system permissions
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

-- 2. ADMIN: acceso total a todos los módulos y acciones
-- ------------------------------------------------------------
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM   roles r
CROSS  JOIN permissions p
WHERE  r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

-- 3. VENDEDOR: permisos base – ver, crear y editar en sus módulos;
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
    'PROSPECTS_VIEW',        'PROSPECTS_CREATE',        'PROSPECTS_EDIT',
    'INVENTORY_VIEW'
)
WHERE  r.name = 'VENDEDOR'
ON CONFLICT DO NOTHING;

-- 4. BODEGA: módulos operativos de inventario
-- ------------------------------------------------------------
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM   roles r
JOIN   permissions p ON p.name IN (
    'INVENTORY_VIEW',       'INVENTORY_CREATE',        'INVENTORY_EDIT',
    'PURCHASE_ORDERS_VIEW'
)
WHERE  r.name = 'BODEGA'
ON CONFLICT DO NOTHING;