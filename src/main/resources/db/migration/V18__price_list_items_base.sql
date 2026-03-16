CREATE TABLE IF NOT EXISTS price_list_items (
    id uuid NOT NULL PRIMARY KEY,
    price_list_id uuid NOT NULL REFERENCES price_lists(id),
    product_id uuid NOT NULL REFERENCES products(id),
    unit_price numeric(18, 2) NOT NULL,
    CONSTRAINT uq_price_list_product UNIQUE (price_list_id, product_id)
);
