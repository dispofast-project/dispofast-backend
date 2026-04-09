-- V21: Add payment terms columns to sales_order
ALTER TABLE sales_order
    ADD COLUMN IF NOT EXISTS payment_condition    VARCHAR(30),
    ADD COLUMN IF NOT EXISTS discount_rate        INTEGER,
    ADD COLUMN IF NOT EXISTS additional_discount_rate NUMERIC(5, 2);
