CREATE TABLE user_commission_rates (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID         NOT NULL,
    category_id UUID         NOT NULL,
    rate        NUMERIC(5,2) NOT NULL CHECK (rate >= 0),
    CONSTRAINT fk_ucr_user     FOREIGN KEY (user_id)     REFERENCES users(id)      ON DELETE CASCADE,
    CONSTRAINT fk_ucr_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT,
    CONSTRAINT uq_user_category UNIQUE (user_id, category_id)
);

CREATE INDEX idx_ucr_user_id ON user_commission_rates(user_id);
