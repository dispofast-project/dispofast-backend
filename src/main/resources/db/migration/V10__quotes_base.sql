CREATE TABLE quotes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    number VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(255) NOT NULL,
    subtotal_amount DOUBLE PRECISION NOT NULL,
    discount_total DOUBLE PRECISION NOT NULL,
    tax_total DOUBLE PRECISION NOT NULL,
    expiration_date TIMESTAMPTZ NOT NULL,
    
    account_id UUID NOT NULL,
    seller_id UUID NOT NULL,
    location_id VARCHAR(10) NOT NULL,
    price_list_id UUID NOT NULL,
    
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    
    CONSTRAINT fk_quotes_account FOREIGN KEY (account_id) REFERENCES accounts(id),
    CONSTRAINT fk_quotes_seller FOREIGN KEY (seller_id) REFERENCES users(id),
    CONSTRAINT fk_quotes_location FOREIGN KEY (location_id) REFERENCES location(city_code),
    CONSTRAINT fk_quotes_price_list FOREIGN KEY (price_list_id) REFERENCES price_lists(id)
);

INSERT INTO quotes (
    id, 
    number, 
    status, 
    subtotal_amount, 
    discount_total, 
    tax_total, 
    expiration_date, 
    account_id, 
    seller_id, 
    location_id, 
    price_list_id
)
VALUES (
    gen_random_uuid(),
    'QT-2024-001',
    'DRAFT',
    100000.00,
    0.00,
    19000.00,
    now() + INTERVAL '30 days',
    '3fa85f64-5717-4562-b3fc-2c963f66afa6',
    (SELECT id FROM users WHERE email = 'admin@dispocol.com' LIMIT 1),
    '11001',
    '3fa85f64-5717-4562-b3fc-2c963f66afa6'
);
