CREATE TABLE user_goals (
    id         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    UUID         NOT NULL,
    type       VARCHAR(30)  NOT NULL CHECK (type IN ('SALES_QUOTA', 'COLLECTION_QUOTA')),
    month      SMALLINT     NOT NULL CHECK (month BETWEEN 1 AND 12),
    year       SMALLINT     NOT NULL CHECK (year >= 2000),
    value      NUMERIC(15,4) NOT NULL CHECK (value >= 0),
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT fk_ug_user    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_user_goal  UNIQUE (user_id, type, month, year)
);

CREATE INDEX idx_user_goals_user_id ON user_goals(user_id);
