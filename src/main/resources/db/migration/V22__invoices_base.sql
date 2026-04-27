CREATE TABLE IF NOT EXISTS invoices (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sales_order_id  UUID REFERENCES sales_order(id),
    invoice_number  VARCHAR(50) NOT NULL,
    client_id       UUID REFERENCES clients(id),
    issue_date      TIMESTAMPTZ NOT NULL,
    total_value     DECIMAL(18,2) NOT NULL,
    pdf_s3_key      VARCHAR(500),
    state           VARCHAR(20) NOT NULL DEFAULT 'ISSUED'
);

CREATE INDEX IF NOT EXISTS idx_invoices_sales_order_id ON invoices(sales_order_id);
CREATE INDEX IF NOT EXISTS idx_invoices_client_id      ON invoices(client_id);
CREATE INDEX IF NOT EXISTS idx_invoices_state          ON invoices(state);
