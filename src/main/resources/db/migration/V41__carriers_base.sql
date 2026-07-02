CREATE TABLE carriers (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name          VARCHAR(100) NOT NULL,
    website       VARCHAR(255),
    registered_at DATE         NOT NULL DEFAULT CURRENT_DATE
);
