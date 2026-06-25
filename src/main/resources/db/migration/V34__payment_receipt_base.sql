-- Track cumulative payments on each AR entry for fast balance queries
ALTER TABLE ar_entries ADD COLUMN paid_amount NUMERIC(18,2) NOT NULL DEFAULT 0;

-- payment_receipt: one row per payment applied to an AR entry
CREATE TABLE payment_receipt (
    id                  UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    receipt_code        VARCHAR(30)   NOT NULL UNIQUE,
    ar_entry_id         UUID          NOT NULL REFERENCES ar_entries(id),
    created_by_user_id  UUID          NOT NULL REFERENCES users(id),
    document_number     VARCHAR(50),
    payment_date        DATE          NOT NULL,
    value               NUMERIC(18,2) NOT NULL CHECK (value > 0),
    payment_method      VARCHAR(20)   NOT NULL CHECK (payment_method IN ('CAJA', 'TRANSFERENCIA')),
    voucher_s3_key      VARCHAR(500),
    observations        TEXT,
    state               VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE' CHECK (state IN ('ACTIVE', 'CANCELLED')),
    created_at          TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_payment_receipt_ar_entry_id  ON payment_receipt(ar_entry_id);
CREATE INDEX IF NOT EXISTS idx_payment_receipt_created_by   ON payment_receipt(created_by_user_id);
CREATE INDEX IF NOT EXISTS idx_payment_receipt_payment_date ON payment_receipt(payment_date);
CREATE INDEX IF NOT EXISTS idx_payment_receipt_state        ON payment_receipt(state);
