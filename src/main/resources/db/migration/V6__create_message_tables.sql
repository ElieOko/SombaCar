CREATE TABLE IF NOT EXISTS support_threads (
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    subject          VARCHAR(255) NOT NULL,
    part_sought      VARCHAR(255) NOT NULL,
    part_reference   VARCHAR(100),
    compatible_brand VARCHAR(100),
    compatible_model VARCHAR(100),
    status           VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_support_threads_user_id ON support_threads (user_id);
CREATE INDEX IF NOT EXISTS idx_support_threads_status ON support_threads (status);

CREATE TABLE IF NOT EXISTS support_messages (
    id          BIGSERIAL PRIMARY KEY,
    thread_id   BIGINT NOT NULL REFERENCES support_threads (id) ON DELETE CASCADE,
    sender_type VARCHAR(50) NOT NULL DEFAULT 'USER',
    sender_id   BIGINT,
    content     TEXT NOT NULL,
    is_read     BOOLEAN NOT NULL DEFAULT FALSE,
    sent_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_support_messages_thread_id ON support_messages (thread_id);
