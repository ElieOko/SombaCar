CREATE TABLE IF NOT EXISTS part_images (
    id               BIGSERIAL PRIMARY KEY,
    part_listing_id  BIGINT REFERENCES part_listings (id) ON DELETE CASCADE,
    name             VARCHAR(255) NOT NULL,
    path             VARCHAR(500) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_part_images_part_listing_id ON part_images (part_listing_id);
