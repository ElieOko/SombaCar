CREATE TABLE IF NOT EXISTS listing_favorites (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    listing_type    VARCHAR(50) NOT NULL,
    listing_id      BIGINT NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, listing_type, listing_id)
);

CREATE INDEX IF NOT EXISTS idx_listing_favorites_user_id ON listing_favorites (user_id);
CREATE INDEX IF NOT EXISTS idx_listing_favorites_listing ON listing_favorites (listing_type, listing_id);

CREATE TABLE IF NOT EXISTS garages (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    name            VARCHAR(255) NOT NULL,
    description     TEXT,
    address         VARCHAR(255),
    city            VARCHAR(100),
    country         VARCHAR(100) NOT NULL DEFAULT 'Democratic Republic of the Congo',
    phone           VARCHAR(50),
    garage_type     VARCHAR(50) NOT NULL DEFAULT 'CAR',
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_garages_user_id ON garages (user_id);
CREATE INDEX IF NOT EXISTS idx_garages_user_id_active ON garages (user_id, is_active);
