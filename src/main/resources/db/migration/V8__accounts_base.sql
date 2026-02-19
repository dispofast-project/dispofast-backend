-- Migration: create accounts table expected by the `Account` entity
CREATE TABLE IF NOT EXISTS accounts (
    id uuid NOT NULL PRIMARY KEY
);

INSERT INTO accounts (id) VALUES ('3fa85f64-5717-4562-b3fc-2c963f66afa6') ON CONFLICT DO NOTHING;
INSERT INTO accounts (id) VALUES ('11111111-1111-1111-1111-111111111111') ON CONFLICT DO NOTHING;
