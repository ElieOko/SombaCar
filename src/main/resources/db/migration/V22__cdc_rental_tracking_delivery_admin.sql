-- CDC VehnixAuto: location, tracking, livraison pièces, documents RDC, champs optionnels

-- Année / kilométrage facultatifs
ALTER TABLE car_listings ALTER COLUMN year DROP NOT NULL;
ALTER TABLE car_listings ALTER COLUMN mileage_km DROP NOT NULL;
ALTER TABLE moto_listings ALTER COLUMN year DROP NOT NULL;
ALTER TABLE moto_listings ALTER COLUMN mileage_km DROP NOT NULL;

-- Champs location enrichis (voitures)
ALTER TABLE car_listings ADD COLUMN IF NOT EXISTS transmission VARCHAR(50);
ALTER TABLE car_listings ADD COLUMN IF NOT EXISTS rent_price_per_week VARCHAR(50);
ALTER TABLE car_listings ADD COLUMN IF NOT EXISTS rent_price_per_month VARCHAR(50);
ALTER TABLE car_listings ADD COLUMN IF NOT EXISTS with_driver BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE car_listings ADD COLUMN IF NOT EXISTS driver_user_id BIGINT REFERENCES users (id) ON DELETE SET NULL;
ALTER TABLE car_listings ADD COLUMN IF NOT EXISTS rental_available BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE moto_listings ADD COLUMN IF NOT EXISTS transmission VARCHAR(50);
ALTER TABLE moto_listings ADD COLUMN IF NOT EXISTS rent_price_per_week VARCHAR(50);
ALTER TABLE moto_listings ADD COLUMN IF NOT EXISTS rent_price_per_month VARCHAR(50);
ALTER TABLE moto_listings ADD COLUMN IF NOT EXISTS with_driver BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE moto_listings ADD COLUMN IF NOT EXISTS driver_user_id BIGINT REFERENCES users (id) ON DELETE SET NULL;
ALTER TABLE moto_listings ADD COLUMN IF NOT EXISTS rental_available BOOLEAN NOT NULL DEFAULT TRUE;

CREATE INDEX IF NOT EXISTS idx_car_listings_driver_user_id ON car_listings (driver_user_id);
CREATE INDEX IF NOT EXISTS idx_moto_listings_driver_user_id ON moto_listings (driver_user_id);

-- Historique des modifications admin
CREATE TABLE IF NOT EXISTS listing_audit_logs (
    id              BIGSERIAL PRIMARY KEY,
    listing_type    VARCHAR(20) NOT NULL,
    listing_id      BIGINT NOT NULL,
    admin_user_id   BIGINT REFERENCES users (id) ON DELETE SET NULL,
    action          VARCHAR(100) NOT NULL,
    changes_json    TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_listing_audit_listing ON listing_audit_logs (listing_type, listing_id);

-- Tracking GPS temps réel (voiture / moto)
CREATE TABLE IF NOT EXISTS vehicle_tracking_events (
    id              BIGSERIAL PRIMARY KEY,
    vehicle_type    VARCHAR(20) NOT NULL,
    vehicle_id      BIGINT NOT NULL,
    user_id         BIGINT REFERENCES users (id) ON DELETE SET NULL,
    driver_user_id  BIGINT REFERENCES users (id) ON DELETE SET NULL,
    latitude        DOUBLE PRECISION,
    longitude       DOUBLE PRECISION,
    tag             VARCHAR(50) NOT NULL DEFAULT 'Depart',
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    message         TEXT NOT NULL,
    rental_request_id BIGINT,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_tracking_vehicle ON vehicle_tracking_events (vehicle_type, vehicle_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_tracking_active ON vehicle_tracking_events (is_active) WHERE is_active = TRUE;

-- Demandes de location
CREATE TABLE IF NOT EXISTS rental_requests (
    id                  BIGSERIAL PRIMARY KEY,
    vehicle_type        VARCHAR(20) NOT NULL,
    vehicle_listing_id  BIGINT NOT NULL,
    client_user_id      BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    driver_user_id      BIGINT REFERENCES users (id) ON DELETE SET NULL,
    start_date          DATE NOT NULL,
    end_date            DATE NOT NULL,
    duration_unit       VARCHAR(20) NOT NULL DEFAULT 'DAYS',
    duration_value      INTEGER NOT NULL DEFAULT 1,
    destinations        TEXT,
    planned_route       TEXT,
    motive              TEXT,
    additional_info     TEXT,
    quoted_amount       VARCHAR(50),
    devise              VARCHAR(10) DEFAULT 'USD',
    payment_link        TEXT,
    payment_reference   VARCHAR(100),
    status              VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    support_thread_id   BIGINT,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_rental_requests_client ON rental_requests (client_user_id);
CREATE INDEX IF NOT EXISTS idx_rental_requests_vehicle ON rental_requests (vehicle_type, vehicle_listing_id);
CREATE INDEX IF NOT EXISTS idx_rental_requests_status ON rental_requests (status);

ALTER TABLE vehicle_tracking_events
    ADD CONSTRAINT fk_tracking_rental_request
    FOREIGN KEY (rental_request_id) REFERENCES rental_requests (id) ON DELETE SET NULL;

-- Livraison pièces
CREATE TABLE IF NOT EXISTS part_deliveries (
    id                  BIGSERIAL PRIMARY KEY,
    part_listing_id     BIGINT REFERENCES part_listings (id) ON DELETE SET NULL,
    buyer_user_id       BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    seller_user_id      BIGINT REFERENCES users (id) ON DELETE SET NULL,
    courier_user_id     BIGINT REFERENCES users (id) ON DELETE SET NULL,
    delivery_address    TEXT NOT NULL,
    city                VARCHAR(100),
    latitude            DOUBLE PRECISION,
    longitude           DOUBLE PRECISION,
    notes               TEXT,
    payment_reference   VARCHAR(100),
    status              VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_part_deliveries_buyer ON part_deliveries (buyer_user_id);
CREATE INDEX IF NOT EXISTS idx_part_deliveries_status ON part_deliveries (status);

CREATE TABLE IF NOT EXISTS part_delivery_tracking_events (
    id              BIGSERIAL PRIMARY KEY,
    delivery_id     BIGINT NOT NULL REFERENCES part_deliveries (id) ON DELETE CASCADE,
    user_id         BIGINT REFERENCES users (id) ON DELETE SET NULL,
    latitude        DOUBLE PRECISION,
    longitude       DOUBLE PRECISION,
    tag             VARCHAR(50) NOT NULL DEFAULT 'Depart',
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    message         TEXT NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_part_delivery_tracking ON part_delivery_tracking_events (delivery_id, created_at DESC);

-- Documents RDC : retirer non adaptés, ajouter manquants
DELETE FROM documents WHERE name IN (
    'Quittance fiscale',
    'Document de gage',
    'Document de non-gage',
    'Certificat de propriété',
    'Certificat VIN',
    'Dédouanement',
    'Taxe routière',
    'Facture d''achat'
);

INSERT INTO documents (name) VALUES
    ('Contrôle technique'),
    ('Vignette'),
    ('Assurance'),
    ('Procès-verbal de vente'),
    ('Carte grise')
ON CONFLICT (name) DO NOTHING;
