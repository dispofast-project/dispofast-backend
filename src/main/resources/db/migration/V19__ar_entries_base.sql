CREATE TABLE IF NOT EXISTS ar_entries (
    id                  uuid          NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id           uuid          NOT NULL REFERENCES clients(id),
    asesor_user_id      uuid                   REFERENCES users(id),
    order_id            uuid                   REFERENCES sales_order(id),
    state               varchar(20)   NOT NULL DEFAULT 'PENDING',
    value               numeric(18,2) NOT NULL,
    invoice_number      varchar(50),
    invoice_date        timestamptz   NOT NULL,
    payment_term_days   int           NOT NULL DEFAULT 30,
    expiration_date     timestamptz   NOT NULL,
    city_id             varchar(10)            REFERENCES cities(code),
    source              varchar(20)   NOT NULL DEFAULT 'ORDER',
    created_at          timestamptz   NOT NULL DEFAULT now(),

    CONSTRAINT uq_ar_entry_order UNIQUE (order_id)
);

CREATE INDEX IF NOT EXISTS idx_ar_entries_client_id    ON ar_entries(client_id);
CREATE INDEX IF NOT EXISTS idx_ar_entries_asesor_id    ON ar_entries(asesor_user_id);
CREATE INDEX IF NOT EXISTS idx_ar_entries_state        ON ar_entries(state);
CREATE INDEX IF NOT EXISTS idx_ar_entries_expiration   ON ar_entries(expiration_date);
CREATE INDEX IF NOT EXISTS idx_ar_entries_invoice_date ON ar_entries(invoice_date);
