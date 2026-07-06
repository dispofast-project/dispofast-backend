-- Speeds up the Cartera search box, which matches client name, invoice
-- number, and order number with a substring pattern (LIKE '%text%'). A
-- leading wildcard can't use a regular btree index, so this adds trigram
-- (GIN) indexes on the searched columns instead.
--
-- Requires the pg_trgm extension. If the database role running migrations
-- lacks privileges to create extensions, this statement will fail — ask a
-- superuser to run `CREATE EXTENSION IF NOT EXISTS pg_trgm;` once, then
-- re-run this migration.
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Indexed on lower(column) to match the `lower(x) LIKE ?` expression the
-- application (Cartera and Clients search) generates.
CREATE INDEX IF NOT EXISTS idx_individuals_first_name_trgm
    ON individuals USING gin (lower(first_name) gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_individuals_last_name_trgm
    ON individuals USING gin (lower(last_name) gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_organizations_legal_name_trgm
    ON organizations USING gin (lower(legal_name) gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_invoices_invoice_number_trgm
    ON invoices USING gin (lower(invoice_number) gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_sales_order_order_number_trgm
    ON sales_order USING gin (lower(order_number) gin_trgm_ops);
