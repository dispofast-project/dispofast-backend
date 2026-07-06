-- Speeds up the Inventory search box, which matches product name, SKU, and
-- reference with a substring pattern (LIKE '%text%'). A leading wildcard
-- can't use a regular btree index, so this adds trigram (GIN) indexes,
-- indexed on lower(column) to match the `lower(x) LIKE ?` expression the
-- application generates. Requires the pg_trgm extension — see
-- V52__cartera_search_trgm_indexes.sql for the same caveat/fallback.
CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX IF NOT EXISTS idx_products_name_trgm
    ON products USING gin (lower(name) gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_products_sku_trgm
    ON products USING gin (lower(sku) gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_products_reference_trgm
    ON products USING gin (lower(reference) gin_trgm_ops);
