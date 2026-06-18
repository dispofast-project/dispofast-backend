CREATE TABLE IF NOT EXISTS shipments (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id          UUID NOT NULL REFERENCES invoices(id),
    state               VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    delivery_address    VARCHAR(500) NOT NULL,
    departure_date      TIMESTAMPTZ,
    delivery_date       TIMESTAMPTZ,
    carrier_id          UUID REFERENCES carriers(id),
    city_code           VARCHAR(10) REFERENCES cities(code)
);

CREATE INDEX IF NOT EXISTS idx_shipments_invoice_id ON shipments(invoice_id);
CREATE INDEX IF NOT EXISTS idx_shipments_carrier_id ON shipments(carrier_id);
CREATE INDEX IF NOT EXISTS idx_shipments_state ON shipments(state);
