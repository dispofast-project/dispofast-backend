-- Optional early-payment ("pronto pago") discount applied to a cash receipt.
-- The rate is a percentage of the associated order's pre-tax subtotal, and the
-- resulting amount is forgiven debt: it counts toward the AR entry's paid_amount
-- alongside the actual cash received.
ALTER TABLE payment_receipt
    ADD COLUMN prompt_payment_discount_rate   SMALLINT CHECK (prompt_payment_discount_rate IN (2, 3, 5)),
    ADD COLUMN prompt_payment_discount_amount NUMERIC(18,2) CHECK (prompt_payment_discount_amount >= 0);
