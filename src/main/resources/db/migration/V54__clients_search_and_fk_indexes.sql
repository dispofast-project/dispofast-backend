-- Speeds up the Clients search box, which matches identification number and
-- email with a substring pattern (LIKE '%text%'); a leading wildcard can't
-- use the existing unique-constraint btree index. Indexed on lower(column)
-- to match the `lower(x) LIKE ?` expression the application generates.
-- (Name search already covered by V52's individuals/organizations indexes.)
-- Requires the pg_trgm extension — see V52 for the same caveat/fallback.
CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX IF NOT EXISTS idx_clients_identification_number_trgm
    ON clients USING gin (lower(identification_number) gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_clients_email_address_trgm
    ON clients USING gin (lower(email_address) gin_trgm_ops);

-- clients.default_advisor_id and clients.city_id are foreign keys with no
-- index (Postgres doesn't create one automatically for the referencing
-- column). The Clients list query now fetch-joins both on every request.
CREATE INDEX IF NOT EXISTS idx_clients_default_advisor_id ON clients(default_advisor_id);
CREATE INDEX IF NOT EXISTS idx_clients_city_id ON clients(city_id);
