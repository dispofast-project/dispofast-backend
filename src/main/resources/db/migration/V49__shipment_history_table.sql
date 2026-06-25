CREATE TABLE IF NOT EXISTS shipment_history (
    id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    shipment_id  UUID         NOT NULL REFERENCES shipments(id) ON DELETE CASCADE,
    changed_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    description  TEXT         NOT NULL,
    user_email   VARCHAR(255) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_shipment_history_shipment_id
    ON shipment_history(shipment_id);
