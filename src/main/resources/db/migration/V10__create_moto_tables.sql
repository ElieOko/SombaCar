CREATE TABLE IF NOT EXISTS moto_models (
    id         BIGSERIAL PRIMARY KEY,
    brand      VARCHAR(100) NOT NULL,
    model      VARCHAR(100) NOT NULL,
    generation VARCHAR(100),
    body_type  VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (brand, model)
);

CREATE INDEX IF NOT EXISTS idx_moto_models_brand ON moto_models (brand);

CREATE TABLE IF NOT EXISTS moto_listings (
    id                    BIGSERIAL PRIMARY KEY,
    user_id               BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    moto_model_id         BIGINT NOT NULL REFERENCES moto_models (id),
    listing_type          VARCHAR(50) NOT NULL DEFAULT 'SALE',
    year                  INTEGER NOT NULL,
    is_electric           BOOLEAN NOT NULL DEFAULT FALSE,
    mileage_km            BIGINT NOT NULL DEFAULT 0,
    "condition"           VARCHAR(50) NOT NULL DEFAULT 'USED',
    plate_number          VARCHAR(50),
    number_vin            VARCHAR(50),
    color                 VARCHAR(50),
    engine_cc             INTEGER,
    price                 VARCHAR(50),
    devise_id             BIGINT,
    rent_price_per_day    VARCHAR(50),
    exchange_description  TEXT,
    description           TEXT,
    city                  VARCHAR(100),
    country               VARCHAR(100) NOT NULL DEFAULT 'Democratic Republic of the Congo',
    status                VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_moto_listings_user_id ON moto_listings (user_id);
CREATE INDEX IF NOT EXISTS idx_moto_listings_moto_model_id ON moto_listings (moto_model_id);
CREATE INDEX IF NOT EXISTS idx_moto_listings_listing_type_status ON moto_listings (listing_type, status);

CREATE TABLE IF NOT EXISTS moto_images (
    id          BIGSERIAL PRIMARY KEY,
    moto_id     BIGINT NOT NULL REFERENCES moto_listings (id) ON DELETE CASCADE,
    name        VARCHAR(255) NOT NULL,
    path        VARCHAR(500) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_moto_images_moto_id ON moto_images (moto_id);

CREATE TABLE IF NOT EXISTS moto_documents (
    id           BIGSERIAL PRIMARY KEY,
    moto_id      BIGINT NOT NULL REFERENCES moto_listings (id) ON DELETE CASCADE,
    document_id  BIGINT NOT NULL REFERENCES documents (id) ON DELETE CASCADE,
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (moto_id, document_id)
);

CREATE INDEX IF NOT EXISTS idx_moto_documents_moto_id ON moto_documents (moto_id);
CREATE INDEX IF NOT EXISTS idx_moto_documents_document_id ON moto_documents (document_id);
