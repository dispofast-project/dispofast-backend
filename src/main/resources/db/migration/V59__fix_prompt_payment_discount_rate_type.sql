-- V57 declared prompt_payment_discount_rate as SMALLINT, but the JPA entity maps it as
-- Integer (INTEGER). Widen the column so Hibernate's schema validation matches.
ALTER TABLE payment_receipt ALTER COLUMN prompt_payment_discount_rate TYPE INTEGER;
