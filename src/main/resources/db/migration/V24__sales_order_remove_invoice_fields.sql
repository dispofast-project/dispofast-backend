-- Invoice data is now managed by the invoices table; remove redundant columns from sales_order
ALTER TABLE sales_order DROP COLUMN IF EXISTS invoice_number;
ALTER TABLE sales_order DROP COLUMN IF EXISTS invoice_url;
