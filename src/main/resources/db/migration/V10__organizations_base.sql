CREATE TABLE organizations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    legal_name VARCHAR(255),
    default_discount_rate INTEGER,
    address TEXT,
    billing_email VARCHAR(255),
    general_email VARCHAR(255),
    phone VARCHAR(20),
    price_list_id UUID,
    location_city_code VARCHAR(10),
    assigned_sales_rep_id UUID,
    CONSTRAINT fk_org_price_list FOREIGN KEY (price_list_id) REFERENCES price_lists(id),
    CONSTRAINT fk_org_location FOREIGN KEY (location_city_code) REFERENCES location(city_code),
    CONSTRAINT fk_org_sales_rep FOREIGN KEY (assigned_sales_rep_id) REFERENCES users(id)
);

INSERT INTO organizations (id, legal_name, default_discount_rate, address, billing_email, general_email, phone, price_list_id, location_city_code, assigned_sales_rep_id)
VALUES (
    '3fa85f64-5717-4562-b3fc-2c963f66afa6',
    'Distribuciones ABC S.A.S',
    5,
    'Cra 45 #26-85, Bogotá',
    'facturacion@abc.com',
    'contacto@abc.com',
    '3001234567',
    '3fa85f64-5717-4562-b3fc-2c963f66afa6',
    '11001',
    (SELECT id FROM users WHERE email = 'vendedor@dispocol.com' LIMIT 1)
);

INSERT INTO organizations (id, legal_name, default_discount_rate, address, billing_email, general_email, phone, price_list_id, location_city_code, assigned_sales_rep_id)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'Constructora XYZ Ltda',
    3,
    'Calle 100 #15-20, Medellín',
    'facturacion@xyz.com',
    'info@xyz.com',
    '3109876543',
    '22222222-2222-2222-2222-222222222222',
    '05001',
    (SELECT id FROM users WHERE email = 'admin@dispocol.com' LIMIT 1)
);
