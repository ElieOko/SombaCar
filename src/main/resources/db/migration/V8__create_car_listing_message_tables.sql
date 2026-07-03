CREATE TABLE IF NOT EXISTS car_listing_threads (
    id              BIGSERIAL PRIMARY KEY,
    car_listing_id  BIGINT NOT NULL REFERENCES car_listings (id) ON DELETE CASCADE,
    buyer_id        BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    seller_id       BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    status          VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (car_listing_id, buyer_id)
);

CREATE INDEX IF NOT EXISTS idx_car_listing_threads_car_listing_id ON car_listing_threads (car_listing_id);
CREATE INDEX IF NOT EXISTS idx_car_listing_threads_buyer_id ON car_listing_threads (buyer_id);
CREATE INDEX IF NOT EXISTS idx_car_listing_threads_seller_id ON car_listing_threads (seller_id);

CREATE TABLE IF NOT EXISTS car_listing_messages (
    id          BIGSERIAL PRIMARY KEY,
    thread_id   BIGINT NOT NULL REFERENCES car_listing_threads (id) ON DELETE CASCADE,
    sender_id   BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    content     TEXT NOT NULL,
    is_read     BOOLEAN NOT NULL DEFAULT FALSE,
    sent_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_car_listing_messages_thread_id ON car_listing_messages (thread_id);
