CREATE TABLE IF NOT EXISTS moto_listing_threads (
    id               BIGSERIAL PRIMARY KEY,
    moto_listing_id  BIGINT NOT NULL REFERENCES moto_listings (id) ON DELETE CASCADE,
    buyer_id         BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    seller_id        BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    status           VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (moto_listing_id, buyer_id)
);

CREATE INDEX IF NOT EXISTS idx_moto_listing_threads_moto_listing_id ON moto_listing_threads (moto_listing_id);
CREATE INDEX IF NOT EXISTS idx_moto_listing_threads_buyer_id ON moto_listing_threads (buyer_id);
CREATE INDEX IF NOT EXISTS idx_moto_listing_threads_seller_id ON moto_listing_threads (seller_id);

CREATE TABLE IF NOT EXISTS moto_listing_messages (
    id          BIGSERIAL PRIMARY KEY,
    thread_id   BIGINT NOT NULL REFERENCES moto_listing_threads (id) ON DELETE CASCADE,
    sender_id   BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    content     TEXT,
    is_read     BOOLEAN NOT NULL DEFAULT FALSE,
    sent_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_moto_listing_messages_thread_id ON moto_listing_messages (thread_id);

CREATE TABLE IF NOT EXISTS moto_listing_message_attachments (
    id          BIGSERIAL PRIMARY KEY,
    message_id  BIGINT NOT NULL REFERENCES moto_listing_messages (id) ON DELETE CASCADE,
    name        VARCHAR(255) NOT NULL,
    path        VARCHAR(500) NOT NULL,
    mime_type   VARCHAR(100)
);

CREATE INDEX IF NOT EXISTS idx_moto_listing_message_attachments_message_id
    ON moto_listing_message_attachments (message_id);

CREATE TABLE IF NOT EXISTS part_listing_threads (
    id               BIGSERIAL PRIMARY KEY,
    part_listing_id  BIGINT NOT NULL REFERENCES part_listings (id) ON DELETE CASCADE,
    buyer_id         BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    seller_id        BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    status           VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (part_listing_id, buyer_id)
);

CREATE INDEX IF NOT EXISTS idx_part_listing_threads_part_listing_id ON part_listing_threads (part_listing_id);
CREATE INDEX IF NOT EXISTS idx_part_listing_threads_buyer_id ON part_listing_threads (buyer_id);
CREATE INDEX IF NOT EXISTS idx_part_listing_threads_seller_id ON part_listing_threads (seller_id);

CREATE TABLE IF NOT EXISTS part_listing_messages (
    id          BIGSERIAL PRIMARY KEY,
    thread_id   BIGINT NOT NULL REFERENCES part_listing_threads (id) ON DELETE CASCADE,
    sender_id   BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    content     TEXT,
    is_read     BOOLEAN NOT NULL DEFAULT FALSE,
    sent_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_part_listing_messages_thread_id ON part_listing_messages (thread_id);

CREATE TABLE IF NOT EXISTS part_listing_message_attachments (
    id          BIGSERIAL PRIMARY KEY,
    message_id  BIGINT NOT NULL REFERENCES part_listing_messages (id) ON DELETE CASCADE,
    name        VARCHAR(255) NOT NULL,
    path        VARCHAR(500) NOT NULL,
    mime_type   VARCHAR(100)
);

CREATE INDEX IF NOT EXISTS idx_part_listing_message_attachments_message_id
    ON part_listing_message_attachments (message_id);
