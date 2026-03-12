CREATE TABLE quotes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    number VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(255) NOT NULL,
    subtotal_amount DOUBLE PRECISION NOT NULL,
    discount_total DOUBLE PRECISION NOT NULL,
    tax_total DOUBLE PRECISION NOT NULL,
    total_amount DOUBLE PRECISION NOT NULL,
    expiration_date TIMESTAMPTZ NOT NULL,
    
    account_id UUID NOT NULL,
    seller_id UUID NOT NULL,
    city_id VARCHAR(10) NOT NULL,
    location_zone VARCHAR(50),
    price_list_id UUID NOT NULL,
    
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    
    CONSTRAINT fk_quotes_account FOREIGN KEY (account_id) REFERENCES clients(id),
    CONSTRAINT fk_quotes_seller FOREIGN KEY (seller_id) REFERENCES users(id),
    CONSTRAINT fk_quotes_location FOREIGN KEY (city_id) REFERENCES cities(code),
    CONSTRAINT fk_quotes_price_list FOREIGN KEY (price_list_id) REFERENCES price_lists(id)
);

-- Seed: 6 quotes variados (diferentes clientes, locations, estados, vendedores)
INSERT INTO quotes (
    id, number, status,
    subtotal_amount, discount_total, tax_total, total_amount,
    expiration_date,
    account_id, seller_id, city_id, location_zone, price_list_id
)
VALUES
-- Quote 1: Individual Bogotá, vendedor, PENDING
(
    gen_random_uuid(),
    'QT-2024-001',
    'PENDING',
    150000.00, 0.00, 28500.00, 178500.00,
    now() + INTERVAL '30 days',
    '44444444-4444-4444-4444-444444444444',
    (SELECT id FROM users WHERE email = 'vendedor@dispocol.com' LIMIT 1),
    '99001', 'CENTRO',
    '3fa85f64-5717-4562-b3fc-2c963f66afa6'
),
-- Quote 2: Individual Medellín, vendedor, ACCEPTED
(
    gen_random_uuid(),
    'QT-2024-002',
    'ACCEPTED',
    200000.00, 10000.00, 36100.00, 226100.00,
    now() + INTERVAL '30 days',
    '55555555-5555-5555-5555-555555555555',
    (SELECT id FROM users WHERE email = 'vendedor@dispocol.com' LIMIT 1),
    '98001', 'NORTE',
    '22222222-2222-2222-2222-222222222222'
),
-- Quote 3: Organization Bogotá, admin, REJECTED
(
    gen_random_uuid(),
    'QT-2024-003',
    'REJECTED',
    50000.00, 2500.00, 9025.00, 56525.00,
    now() + INTERVAL '30 days',
    '3fa85f64-5717-4562-b3fc-2c963f66afa6',
    (SELECT id FROM users WHERE email = 'admin@dispocol.com' LIMIT 1),
    '99001', 'CENTRO',
    '3fa85f64-5717-4562-b3fc-2c963f66afa6'
),
-- Quote 4: Organization Medellín, admin, EXPIRED
(
    gen_random_uuid(),
    'QT-2024-004',
    'EXPIRED',
    120000.00, 3600.00, 22116.00, 138516.00,
    now() - INTERVAL '5 days',
    '11111111-1111-1111-1111-111111111111',
    (SELECT id FROM users WHERE email = 'admin@dispocol.com' LIMIT 1),
    '98001', 'NORTE',
    '22222222-2222-2222-2222-222222222222'
),
-- Quote 5: Organization Bogotá, vendedor, PENDING
(
    gen_random_uuid(),
    'QT-2024-005',
    'PENDING',
    350000.00, 17500.00, 63175.00, 395675.00,
    now() + INTERVAL '15 days',
    '3fa85f64-5717-4562-b3fc-2c963f66afa6',
    (SELECT id FROM users WHERE email = 'vendedor@dispocol.com' LIMIT 1),
    '99001', 'CENTRO',
    '3fa85f64-5717-4562-b3fc-2c963f66afa6'
),
-- Quote 6: Individual Medellín, admin, ACCEPTED
(
    gen_random_uuid(),
    'QT-2024-006',
    'ACCEPTED',
    85000.00, 0.00, 16150.00, 101150.00,
    now() + INTERVAL '20 days',
    '55555555-5555-5555-5555-555555555555',
    (SELECT id FROM users WHERE email = 'admin@dispocol.com' LIMIT 1),
    '98001', 'NORTE',
    '22222222-2222-2222-2222-222222222222'
);
