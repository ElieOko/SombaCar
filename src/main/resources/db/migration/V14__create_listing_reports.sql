CREATE TABLE IF NOT EXISTS listing_reports (
    id              BIGSERIAL PRIMARY KEY,
    listing_type    VARCHAR(50) NOT NULL,
    listing_id      BIGINT NOT NULL,
    reported_by     BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    reason          TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (reported_by, listing_type, listing_id)
);

CREATE INDEX IF NOT EXISTS idx_listing_reports_listing ON listing_reports (listing_type, listing_id);
