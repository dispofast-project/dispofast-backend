CREATE TABLE individuals (
    id UUID PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    rep_first_name VARCHAR(255),
    rep_last_name VARCHAR(255),
    rep_identification VARCHAR(100),
    rep_job_title VARCHAR(255),
    rep_email VARCHAR(255),
    rep_phone VARCHAR(50),
    CONSTRAINT fk_indiv_client FOREIGN KEY (id) REFERENCES clients(id) ON DELETE CASCADE
);

INSERT INTO clients (id, legal_entity_type, identification_number, email_address, phone_number, is_active, retefuente_applies, address, default_advisor_id, location_id, default_discount_rate, price_list_id, client_type_id)
VALUES (
    '44444444-4444-4444-4444-444444444444',
    'NATURAL',
    '1020304050',
    'carlos.ramirez@abc.com',
    '3001234599',
    true,
    false,
    'Calle 80 #10-10, Bogotá',
    (SELECT id FROM users WHERE email = 'vendedor@dispocol.com' LIMIT 1),
    '11001',
    0,
    '3fa85f64-5717-4562-b3fc-2c963f66afa6',
    1
);

INSERT INTO individuals (id, first_name, last_name, rep_first_name, rep_last_name, rep_identification, rep_job_title, rep_email, rep_phone)
VALUES (
    '44444444-4444-4444-4444-444444444444',
    'Carlos',
    'Ramírez',
    NULL, NULL, NULL, NULL, NULL, NULL
);

INSERT INTO clients (id, legal_entity_type, identification_number, email_address, phone_number, is_active, retefuente_applies, address, default_advisor_id, location_id, default_discount_rate, price_list_id, client_type_id)
VALUES (
    '55555555-5555-5555-5555-555555555555',
    'NATURAL',
    '9080706050',
    'maria.gonzalez@xyz.com',
    '3109876599',
    true,
    true,
    'Cra 50 #30-30, Medellín',
    (SELECT id FROM users WHERE email = 'admin@dispocol.com' LIMIT 1),
    '05001',
    0,
    '22222222-2222-2222-2222-222222222222',
    2
);

INSERT INTO individuals (id, first_name, last_name, rep_first_name, rep_last_name, rep_identification, rep_job_title, rep_email, rep_phone)
VALUES (
    '55555555-5555-5555-5555-555555555555',
    'María',
    'González',
    NULL, NULL, NULL, NULL, NULL, NULL
);
