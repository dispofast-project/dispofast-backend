CREATE TABLE shipment_items (
    id           UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    shipment_id  UUID          NOT NULL REFERENCES shipments(id) ON DELETE CASCADE,
    product_id   UUID,
    product_sku  VARCHAR(100),
    product_name VARCHAR(500)  NOT NULL,
    quantity     NUMERIC(12,2) NOT NULL,
    unit_price   NUMERIC(18,2) NOT NULL,
    tax_percent  INTEGER       NOT NULL DEFAULT 0,
    line_total   NUMERIC(18,2) NOT NULL
);

CREATE INDEX idx_shipment_items_shipment_id ON shipment_items(shipment_id);
