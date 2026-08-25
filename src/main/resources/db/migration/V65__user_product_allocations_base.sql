CREATE TABLE user_product_allocations (
    id                UUID    PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id           UUID    NOT NULL,
    product_id        UUID    NOT NULL,
    assigned_quantity INTEGER NOT NULL CHECK (assigned_quantity >= 0),
    consumed_quantity INTEGER NOT NULL DEFAULT 0 CHECK (consumed_quantity >= 0),

    CONSTRAINT fk_upa_user    FOREIGN KEY (user_id)    REFERENCES users(id)    ON DELETE CASCADE,
    CONSTRAINT fk_upa_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT uq_user_product_allocation UNIQUE (user_id, product_id)
);

CREATE INDEX idx_upa_user_id ON user_product_allocations(user_id);
