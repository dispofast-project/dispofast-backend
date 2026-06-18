CREATE TABLE IF NOT EXISTS shipment_items (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    shipment_id  UUID NOT NULL REFERENCES shipments(id) ON DELETE CASCADE,
    product_id   UUID REFERENCES products(id),
    product_sku  VARCHAR(100),
    product_name VARCHAR(500) NOT NULL,
    quantity     DECIMAL(12,2) NOT NULL,
    unit_price   DECIMAL(18,2) NOT NULL,
    tax_percent  INTEGER NOT NULL DEFAULT 0,
    line_total   DECIMAL(18,2) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_shipment_items_shipment_id ON shipment_items(shipment_id);

-- Backfill: clonar ítems de los despachos existentes que ya tienen orden de venta
INSERT INTO shipment_items (shipment_id, product_id, product_sku, product_name, quantity, unit_price, tax_percent, line_total)
SELECT
    s.id,
    p.id,
    p.sku,
    p.name,
    soi.quantity,
    soi.unit_price,
    CASE WHEN p.tax_free THEN 0 ELSE 19 END,
    soi.line_total
FROM shipments s
JOIN invoices i        ON i.id         = s.invoice_id
JOIN sales_order so    ON so.id        = i.sales_order_id
JOIN sales_order_item soi ON soi.order_id = so.id
JOIN products p        ON p.id         = soi.product_id
ON CONFLICT DO NOTHING;
