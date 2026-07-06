-- Speeds up the Quotes search box, which matches quote number and seller
-- full name with a substring pattern (LIKE '%text%'); a leading wildcard
-- can't use the existing unique-constraint btree index on `number`, and
-- `users.full_name` has no index at all. Indexed on lower(column) to match
-- the `lower(x) LIKE ?` expression the application generates.
-- (Client identification-number search already covered by V54.)
-- Requires the pg_trgm extension — see V52 for the same caveat/fallback.
CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX IF NOT EXISTS idx_quotes_number_trgm
    ON quotes USING gin (lower(number) gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_users_full_name_trgm
    ON users USING gin (lower(full_name) gin_trgm_ops);

-- quotes.account_id and quotes.seller_id are foreign keys with no index.
-- The Quotes list query now fetch-joins both (via @EntityGraph) on every
-- request, and searchByTextAndSeller filters on seller_id directly.
CREATE INDEX IF NOT EXISTS idx_quotes_account_id ON quotes(account_id);
CREATE INDEX IF NOT EXISTS idx_quotes_seller_id ON quotes(seller_id);
