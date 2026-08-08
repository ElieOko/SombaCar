CREATE TABLE IF NOT EXISTS sale_offers (
    id                  BIGSERIAL PRIMARY KEY,
    offer_type          VARCHAR(50) NOT NULL,
    title               VARCHAR(255) NOT NULL,
    description         TEXT,
    price               VARCHAR(50) NOT NULL,
    devise              VARCHAR(10) NOT NULL DEFAULT 'USD',
    linked_listing_id   BIGINT,
    status              VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_by          BIGINT REFERENCES users (id) ON DELETE SET NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_sale_offers_offer_type_status ON sale_offers (offer_type, status);
CREATE INDEX IF NOT EXISTS idx_sale_offers_status ON sale_offers (status);

CREATE TABLE IF NOT EXISTS type_cards (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE
);

ALTER TABLE paiements
    ADD COLUMN IF NOT EXISTS offer_id BIGINT REFERENCES sale_offers (id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS order_number VARCHAR(255),
    ADD COLUMN IF NOT EXISTS purchase_type VARCHAR(50);

CREATE INDEX IF NOT EXISTS idx_paiements_offer_id ON paiements (offer_id);
