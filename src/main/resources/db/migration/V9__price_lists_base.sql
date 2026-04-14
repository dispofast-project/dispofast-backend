CREATE TABLE IF NOT EXISTS price_lists (
    id uuid NOT NULL PRIMARY KEY,
    name varchar(255) NOT NULL
);

INSERT INTO price_lists (id, name) VALUES ('3fa85f64-5717-4562-b3fc-2c963f66afa6', 'Lista Base') ON CONFLICT DO NOTHING;
INSERT INTO price_lists (id, name) VALUES ('22222222-2222-2222-2222-222222222222', 'Lista Premium') ON CONFLICT DO NOTHING;
