CREATE TABLE IF NOT EXISTS quote_items (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quote_id         UUID NOT NULL,
    product_id       UUID NOT NULL,
    quantity         DECIMAL(12,2) NOT NULL,
    unit_price       DECIMAL(18,2) NOT NULL,
    tax_rate         DECIMAL(7,4)  NOT NULL DEFAULT 0.19,
    tax_amount       DECIMAL(18,2) NOT NULL DEFAULT 0,
    line_total       DECIMAL(18,2) NOT NULL,

    CONSTRAINT fk_quote_item_quote   FOREIGN KEY (quote_id)   REFERENCES quotes(id) ON DELETE CASCADE,
    CONSTRAINT fk_quote_item_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE INDEX IF NOT EXISTS idx_quote_items_quote_id ON quote_items(quote_id);
