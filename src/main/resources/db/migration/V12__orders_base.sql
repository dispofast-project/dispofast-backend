-- Add name column to accounts to match the Account entity
ALTER TABLE accounts ADD COLUMN IF NOT EXISTS name VARCHAR(255);

-- Update existing seeded accounts with placeholder names
UPDATE accounts SET name = 'Cuenta Principal' WHERE id = '3fa85f64-5717-4562-b3fc-2c963f66afa6' AND name IS NULL;
UPDATE accounts SET name = 'Cuenta Secundaria' WHERE id = '11111111-1111-1111-1111-111111111111' AND name IS NULL;

-- sales_order table
CREATE TABLE IF NOT EXISTS sales_order (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_number         VARCHAR(20) UNIQUE,
    account_id           UUID,
    asesor_user_id       UUID,
    state                VARCHAR(20),
    order_date           TIMESTAMPTZ,
    shipment_city_id     VARCHAR(10),
    shipment_address     TEXT,
    zone                 VARCHAR(50),
    total_value          DECIMAL(18,2),
    account_price_list_id UUID,
    quote_id             UUID,
    invoice_number       VARCHAR(50),
    invoice_url          VARCHAR(500),

    CONSTRAINT fk_order_account      FOREIGN KEY (account_id)           REFERENCES accounts(id),
    CONSTRAINT fk_order_asesor       FOREIGN KEY (asesor_user_id)        REFERENCES users(id),
    CONSTRAINT fk_order_city         FOREIGN KEY (shipment_city_id)      REFERENCES location(city_code),
    CONSTRAINT fk_order_price_list   FOREIGN KEY (account_price_list_id) REFERENCES price_lists(id),
    CONSTRAINT fk_order_quote        FOREIGN KEY (quote_id)              REFERENCES quotes(id)
);

CREATE INDEX IF NOT EXISTS idx_sales_order_state          ON sales_order(state);
CREATE INDEX IF NOT EXISTS idx_sales_order_account_id     ON sales_order(account_id);
CREATE INDEX IF NOT EXISTS idx_sales_order_asesor_user_id ON sales_order(asesor_user_id);
CREATE INDEX IF NOT EXISTS idx_sales_order_order_number   ON sales_order(order_number);
CREATE INDEX IF NOT EXISTS idx_sales_order_quote_id       ON sales_order(quote_id);

-- sales_order_item table
CREATE TABLE IF NOT EXISTS sales_order_item (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id   UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity   DECIMAL(12,2),
    unit_price DECIMAL(18,2),
    discount   DECIMAL(18,2) DEFAULT 0,
    line_total DECIMAL(18,2),

    CONSTRAINT fk_order_item_order   FOREIGN KEY (order_id)   REFERENCES sales_order(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_item_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE INDEX IF NOT EXISTS idx_sales_order_item_order_id ON sales_order_item(order_id);
