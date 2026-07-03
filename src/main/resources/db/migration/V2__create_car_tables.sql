CREATE TABLE IF NOT EXISTS car_models (
    id         BIGSERIAL PRIMARY KEY,
    brand      VARCHAR(100) NOT NULL,
    model      VARCHAR(100) NOT NULL,
    generation VARCHAR(100),
    body_type  VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (brand, model)
);

CREATE INDEX IF NOT EXISTS idx_car_models_brand ON car_models (brand);

CREATE TABLE IF NOT EXISTS car_listings (
    id                    BIGSERIAL PRIMARY KEY,
    user_id               BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    car_model_id          BIGINT NOT NULL REFERENCES car_models (id),
    listing_type          VARCHAR(50) NOT NULL DEFAULT 'SALE',
    year                  INTEGER NOT NULL,
    is_electric           BOOLEAN NOT NULL DEFAULT FALSE,
    mileage_km            BIGINT NOT NULL DEFAULT 0,
    "condition"           VARCHAR(50) NOT NULL DEFAULT 'USED',
    plate_number          VARCHAR(50),
    color                 VARCHAR(50),
    seats                 INTEGER,
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

CREATE INDEX IF NOT EXISTS idx_car_listings_user_id ON car_listings (user_id);
CREATE INDEX IF NOT EXISTS idx_car_listings_car_model_id ON car_listings (car_model_id);
CREATE INDEX IF NOT EXISTS idx_car_listings_listing_type_status ON car_listings (listing_type, status);

CREATE TABLE IF NOT EXISTS car_images (
    id          BIGSERIAL PRIMARY KEY,
    car_id BIGINT REFERENCES car_listings (id) ON DELETE CASCADE,
    name        VARCHAR(255) NOT NULL,
    path        VARCHAR(500) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_car_images_property_id ON car_images (car_id);
