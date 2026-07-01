CREATE TABLE vehicles (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    plate      VARCHAR(50) NOT NULL UNIQUE,
    state      VARCHAR(30) NOT NULL,
    type       VARCHAR(30) NOT NULL,
    created_at DATE
);
