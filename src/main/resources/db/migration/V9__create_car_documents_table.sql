CREATE TABLE IF NOT EXISTS documents (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO documents (name) VALUES
    ('Carte grise'),
    ('Assurance'),
    ('Contrôle technique'),
    ('Certificat de propriété'),
    ('Facture d''achat'),
    ('Certificat VIN'),
    ('Dédouanement'),
    ('Taxe routière')
ON CONFLICT (name) DO NOTHING;

CREATE TABLE IF NOT EXISTS car_documents (
    id           BIGSERIAL PRIMARY KEY,
    car_id       BIGINT NOT NULL REFERENCES car_listings (id) ON DELETE CASCADE,
    document_id  BIGINT NOT NULL REFERENCES documents (id) ON DELETE CASCADE,
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (car_id, document_id)
);

CREATE INDEX IF NOT EXISTS idx_car_documents_car_id ON car_documents (car_id);
CREATE INDEX IF NOT EXISTS idx_car_documents_document_id ON car_documents (document_id);
