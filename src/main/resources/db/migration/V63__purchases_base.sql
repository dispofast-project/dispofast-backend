CREATE TABLE purchase_orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    number VARCHAR(255) NOT NULL UNIQUE,
    payment_condition VARCHAR(50),

    supplier_id UUID NOT NULL,
    buyer_id UUID NOT NULL,

    subtotal_amount              DECIMAL(18,2) NOT NULL DEFAULT 0,
    commercial_discount_rate     DECIMAL(7,4)  NOT NULL DEFAULT 0,
    commercial_discount_amount   DECIMAL(18,2) NOT NULL DEFAULT 0,
    other_discounts_rate         DECIMAL(7,4)  NOT NULL DEFAULT 0,
    other_discounts_amount       DECIMAL(18,2) NOT NULL DEFAULT 0,
    iva_rate                     DECIMAL(7,4)  NOT NULL DEFAULT 0.19,
    iva_amount                   DECIMAL(18,2) NOT NULL DEFAULT 0,
    retefuente_rate              DECIMAL(7,4),
    retefuente_amount            DECIMAL(18,2),
    retefuente_type_override     VARCHAR(20),
    total_amount                 DECIMAL(18,2) NOT NULL DEFAULT 0,
    freight                      DECIMAL(18,2) NOT NULL DEFAULT 0,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_purchase_orders_supplier FOREIGN KEY (supplier_id) REFERENCES clients(id),
    CONSTRAINT fk_purchase_orders_buyer    FOREIGN KEY (buyer_id)    REFERENCES users(id)
);

CREATE TABLE purchase_order_items (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    purchase_order_id  UUID NOT NULL,
    product_id         UUID NOT NULL,
    quantity           DECIMAL(12,2) NOT NULL,
    unit_price         DECIMAL(18,2) NOT NULL,
    tax_rate           DECIMAL(7,4)  NOT NULL DEFAULT 0.19,
    tax_amount         DECIMAL(18,2) NOT NULL DEFAULT 0,
    line_total         DECIMAL(18,2) NOT NULL,

    CONSTRAINT fk_purchase_order_item_order   FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_purchase_order_item_product FOREIGN KEY (product_id)        REFERENCES products(id)
);

CREATE INDEX idx_purchase_order_items_order_id ON purchase_order_items(purchase_order_id);

-- Búsqueda por número / proveedor / comprador, igual que en quotes (ver V55).
CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX idx_purchase_orders_number_trgm
    ON purchase_orders USING gin (lower(number) gin_trgm_ops);

CREATE INDEX idx_purchase_orders_supplier_id ON purchase_orders(supplier_id);
CREATE INDEX idx_purchase_orders_buyer_id ON purchase_orders(buyer_id);
