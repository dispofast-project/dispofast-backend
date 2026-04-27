-- V22: Add financial breakdown columns to sales_order
ALTER TABLE sales_order
    ADD COLUMN IF NOT EXISTS tax_amount        NUMERIC(18, 2),
    ADD COLUMN IF NOT EXISTS retefuente_amount NUMERIC(18, 2),
    ADD COLUMN IF NOT EXISTS freight           NUMERIC(18, 2);
