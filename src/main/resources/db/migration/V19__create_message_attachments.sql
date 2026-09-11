CREATE TABLE IF NOT EXISTS car_listing_message_attachments (
    id          BIGSERIAL PRIMARY KEY,
    message_id  BIGINT NOT NULL REFERENCES car_listing_messages (id) ON DELETE CASCADE,
    name        VARCHAR(255) NOT NULL,
    path        VARCHAR(500) NOT NULL,
    mime_type   VARCHAR(100)
);

CREATE INDEX IF NOT EXISTS idx_car_listing_message_attachments_message_id
    ON car_listing_message_attachments (message_id);

CREATE TABLE IF NOT EXISTS support_message_attachments (
    id          BIGSERIAL PRIMARY KEY,
    message_id  BIGINT NOT NULL REFERENCES support_messages (id) ON DELETE CASCADE,
    name        VARCHAR(255) NOT NULL,
    path        VARCHAR(500) NOT NULL,
    mime_type   VARCHAR(100)
);

CREATE INDEX IF NOT EXISTS idx_support_message_attachments_message_id
    ON support_message_attachments (message_id);

ALTER TABLE car_listing_messages
    ALTER COLUMN content DROP NOT NULL;

ALTER TABLE support_messages
    ALTER COLUMN content DROP NOT NULL;
