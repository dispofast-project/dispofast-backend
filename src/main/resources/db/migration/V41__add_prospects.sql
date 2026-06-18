CREATE TABLE prospects (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(255) NOT NULL,
    legal_entity_type VARCHAR(50) NOT NULL,
    client_type_id    BIGINT REFERENCES client_types(id),
    phone       VARCHAR(50),
    email       VARCHAR(255)
);

ALTER TABLE quotes
    ALTER COLUMN account_id DROP NOT NULL,
    ADD COLUMN prospect_id UUID REFERENCES prospects(id);
