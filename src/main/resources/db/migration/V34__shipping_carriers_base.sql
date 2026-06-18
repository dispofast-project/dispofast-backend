CREATE TABLE IF NOT EXISTS carriers (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(100) NOT NULL,
    plate           VARCHAR(50) NOT NULL UNIQUE
);

CREATE INDEX IF NOT EXISTS idx_carriers_plate ON carriers(plate);
