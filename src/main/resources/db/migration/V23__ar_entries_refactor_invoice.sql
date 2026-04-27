-- Migrate existing ar_entries that have invoice data to the new invoices table
INSERT INTO invoices (id, sales_order_id, invoice_number, client_id, issue_date, total_value, state)
SELECT
    gen_random_uuid(),
    ae.order_id,
    ae.invoice_number,
    ae.client_id,
    ae.invoice_date,
    ae.value,
    'ISSUED'
FROM ar_entries ae
WHERE ae.invoice_number IS NOT NULL;

-- Add invoice_id FK column to ar_entries
ALTER TABLE ar_entries ADD COLUMN invoice_id UUID REFERENCES invoices(id);

-- Link existing entries to the newly created invoices
UPDATE ar_entries ae
SET invoice_id = i.id
FROM invoices i
WHERE i.sales_order_id = ae.order_id
  AND ae.invoice_number IS NOT NULL;

-- Drop the now-redundant columns
ALTER TABLE ar_entries DROP COLUMN IF EXISTS invoice_number;
ALTER TABLE ar_entries DROP COLUMN IF EXISTS invoice_date;

CREATE INDEX IF NOT EXISTS idx_ar_entries_invoice_id ON ar_entries(invoice_id);
