CREATE TABLE IF NOT EXISTS cart_items (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    part_type           VARCHAR(50) NOT NULL,
    tools_id            BIGINT NOT NULL REFERENCES part_listings (id) ON DELETE CASCADE,
    quantity            INT NOT NULL DEFAULT 1,
    unit_price          VARCHAR(50) NOT NULL,
    total_price         VARCHAR(50) NOT NULL,
    devise              VARCHAR(10) NOT NULL DEFAULT 'USD',
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    payment_reference   VARCHAR(255),
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_cart_items_user_id_active ON cart_items (user_id, is_active);
CREATE INDEX IF NOT EXISTS idx_cart_items_payment_reference ON cart_items (payment_reference);
CREATE INDEX IF NOT EXISTS idx_cart_items_tools_id ON cart_items (tools_id);
