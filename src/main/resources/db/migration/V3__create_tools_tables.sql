CREATE TABLE IF NOT EXISTS part_listings (
    id                    BIGSERIAL PRIMARY KEY,
    user_id               BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    name                  VARCHAR(255) NOT NULL,
    part_reference        VARCHAR(100),
    brand                 VARCHAR(100),
    compatible_brand      VARCHAR(100),
    compatible_model      VARCHAR(100),
    listing_type          VARCHAR(50) NOT NULL DEFAULT 'SALE',
    "condition"           VARCHAR(50) NOT NULL DEFAULT 'USED',
    price                 VARCHAR(50),
    rent_price_per_day    VARCHAR(50),
    exchange_description  TEXT,
    description           TEXT,
    city                  VARCHAR(100),
    country               VARCHAR(100) NOT NULL DEFAULT 'Democratic Republic of the Congo',
    status                VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_part_listings_user_id ON part_listings (user_id);
CREATE INDEX IF NOT EXISTS idx_part_listings_listing_type_status ON part_listings (listing_type, status);
CREATE INDEX IF NOT EXISTS idx_part_listings_name ON part_listings (name);
