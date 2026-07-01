CREATE TABLE shipments (
    id                      UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id              UUID         NOT NULL,
    state                   VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    delivery_address        VARCHAR(500) NOT NULL,
    address_detail          VARCHAR(255),
    departure_date          TIMESTAMPTZ,
    delivery_date           TIMESTAMPTZ,
    carrier_id              UUID         REFERENCES carriers(id),
    driver_id               UUID         REFERENCES drivers(id),
    city_code               VARCHAR(10)  REFERENCES cities(code),
    invoice_number          VARCHAR(50),
    created_at              TIMESTAMPTZ  NOT NULL DEFAULT now(),
    client_name             VARCHAR(255),
    asesor_name             VARCHAR(255),
    estimated_delivery_date DATE,
    product_count           INTEGER,
    delivery_type           VARCHAR(20),
    tracking_code           VARCHAR(100)
);

CREATE INDEX idx_shipments_state      ON shipments(state);
CREATE INDEX idx_shipments_invoice_id ON shipments(invoice_id);
CREATE INDEX idx_shipments_carrier_id ON shipments(carrier_id);
CREATE INDEX idx_shipments_driver_id  ON shipments(driver_id);
