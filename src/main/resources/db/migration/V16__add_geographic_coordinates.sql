ALTER TABLE garages
    ADD COLUMN IF NOT EXISTS latitude DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS longitude DOUBLE PRECISION;

ALTER TABLE car_listings
    ADD COLUMN IF NOT EXISTS latitude DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS longitude DOUBLE PRECISION;

ALTER TABLE moto_listings
    ADD COLUMN IF NOT EXISTS latitude DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS longitude DOUBLE PRECISION;

ALTER TABLE part_listings
    ADD COLUMN IF NOT EXISTS latitude DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS longitude DOUBLE PRECISION;

CREATE INDEX IF NOT EXISTS idx_garages_coordinates ON garages (latitude, longitude);
CREATE INDEX IF NOT EXISTS idx_car_listings_coordinates ON car_listings (latitude, longitude);
CREATE INDEX IF NOT EXISTS idx_moto_listings_coordinates ON moto_listings (latitude, longitude);
CREATE INDEX IF NOT EXISTS idx_part_listings_coordinates ON part_listings (latitude, longitude);
