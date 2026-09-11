CREATE TABLE IF NOT EXISTS garage_images (
    id         BIGSERIAL PRIMARY KEY,
    garage_id  BIGINT NOT NULL REFERENCES garages (id) ON DELETE CASCADE,
    name       VARCHAR(255) NOT NULL,
    path       VARCHAR(500) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_garage_images_garage_id ON garage_images (garage_id);

CREATE TABLE IF NOT EXISTS mechanic_images (
    id           BIGSERIAL PRIMARY KEY,
    mechanic_id  BIGINT NOT NULL REFERENCES mechanics (id) ON DELETE CASCADE,
    name         VARCHAR(255) NOT NULL,
    path         VARCHAR(500) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_mechanic_images_mechanic_id ON mechanic_images (mechanic_id);
