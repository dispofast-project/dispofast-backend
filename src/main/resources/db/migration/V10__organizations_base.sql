CREATE TABLE clients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    legal_entity_type VARCHAR(50) NOT NULL,
    identification_number VARCHAR(100) UNIQUE NOT NULL,
    email_address VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(50) UNIQUE NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    retefuente_applies BOOLEAN NOT NULL DEFAULT false,
    address TEXT NOT NULL,
    default_advisor_id UUID NOT NULL,
    city_id VARCHAR(10) NOT NULL,
    location_zone VARCHAR(50),
    default_discount_rate INTEGER,
    price_list_id UUID NOT NULL,
    client_type_id BIGINT NOT NULL,

    CONSTRAINT fk_client_advisor FOREIGN KEY (default_advisor_id) REFERENCES users(id),
    CONSTRAINT fk_client_location FOREIGN KEY (city_id) REFERENCES cities(code),
    CONSTRAINT fk_client_price_list FOREIGN KEY (price_list_id) REFERENCES price_lists(id),
    CONSTRAINT fk_client_type FOREIGN KEY (client_type_id) REFERENCES client_types(id)
);

CREATE TABLE organizations (
    id UUID PRIMARY KEY,
    legal_name VARCHAR(255),
    billing_email VARCHAR(255),
    rep_first_name VARCHAR(255),
    rep_last_name VARCHAR(255),
    rep_identification VARCHAR(255),
    rep_email VARCHAR(255),
    rep_phone VARCHAR(255),
    
    CONSTRAINT fk_org_client FOREIGN KEY (id) REFERENCES clients(id) ON DELETE CASCADE
);

-- Seed: Organization 1 (Distribuidor en Bogotá)
INSERT INTO clients (id, legal_entity_type, identification_number, email_address, phone_number, is_active, retefuente_applies, address, default_advisor_id, city_id, location_zone, default_discount_rate, price_list_id, client_type_id)
VALUES (
    '3fa85f64-5717-4562-b3fc-2c963f66afa6',
    'LEGAL',
    '900123456-1',
    'contacto@abc.com',
    '3001234567',
    true,
    false,
    'Cra 45 #26-85, Bogotá',
    (SELECT id FROM users WHERE email = 'vendedor@dispocol.com' LIMIT 1),
    '99001',
    'CENTRO',
    5,
    '3fa85f64-5717-4562-b3fc-2c963f66afa6',
    1
);

INSERT INTO organizations (id, legal_name, billing_email, rep_first_name, rep_last_name, rep_identification, rep_email, rep_phone)
VALUES (
    '3fa85f64-5717-4562-b3fc-2c963f66afa6',
    'Distribuciones ABC S.A.S',
    'facturacion@abc.com',
    'Carlos',
    'Gutiérrez',
    '1020304050',
    'cgutierrez@abc.com',
    '3001239988'
);

-- Seed: Organization 2 (Constructor en Medellín)
INSERT INTO clients (id, legal_entity_type, identification_number, email_address, phone_number, is_active, retefuente_applies, address, default_advisor_id, city_id, location_zone, default_discount_rate, price_list_id, client_type_id)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'LEGAL',
    '800987654-2',
    'info@xyz.com',
    '3109876543',
    true,
    true,
    'Calle 100 #15-20, Medellín',
    (SELECT id FROM users WHERE email = 'admin@dispocol.com' LIMIT 1),
    '98001',
    'NORTE',
    3,
    '22222222-2222-2222-2222-222222222222',
    2
);

INSERT INTO organizations (id, legal_name, billing_email, rep_first_name, rep_last_name, rep_identification, rep_email, rep_phone)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'Constructora XYZ Ltda',
    'facturacion@xyz.com',
    'María',
    'López',
    '5060708090',
    'mlopez@xyz.com',
    '3109871122'
);
