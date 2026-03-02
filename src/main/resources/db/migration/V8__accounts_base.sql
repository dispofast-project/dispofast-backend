-- Client types (lookup table)
CREATE SEQUENCE client_types_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE client_types (
    id BIGINT NOT NULL DEFAULT nextval('client_types_seq') PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

INSERT INTO client_types (id, name) VALUES (1, 'Distribuidor');
INSERT INTO client_types (id, name) VALUES (2, 'Constructor');

-- Media assets (standalone, used by legal_documents)
CREATE TABLE media_assets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    filename VARCHAR(255),
    storage_path TEXT,
    mime_type VARCHAR(100),
    file_size BIGINT,
    type VARCHAR(255),
    metadata JSONB,
    
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
