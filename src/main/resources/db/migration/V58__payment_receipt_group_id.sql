-- Groups several payment_receipt rows that were created from a single cash payment
-- applied across multiple invoices (AR entries) of the same client. NULL for
-- single-invoice receipts, same value shared by all receipts of a combined payment.
ALTER TABLE payment_receipt ADD COLUMN payment_group_id UUID;

CREATE INDEX IF NOT EXISTS idx_payment_receipt_payment_group_id ON payment_receipt(payment_group_id);
