CREATE TABLE IF NOT EXISTS system_params (
    id           SERIAL PRIMARY KEY,
    clave        VARCHAR(50)    NOT NULL UNIQUE,
    valor        DECIMAL(18,4)  NOT NULL,
    last_updated TIMESTAMPTZ    NOT NULL DEFAULT now()
);

INSERT INTO system_params (clave, valor) VALUES ('IVA', 0.1900);
