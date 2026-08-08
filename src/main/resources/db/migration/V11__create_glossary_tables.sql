CREATE TABLE IF NOT EXISTS glossary_entries (
    id             BIGSERIAL PRIMARY KEY,
    official_name  VARCHAR(255) NOT NULL,
    category       VARCHAR(150) NOT NULL,
    local_names    TEXT NOT NULL DEFAULT '[]',
    description    TEXT,
    wear_signs     TEXT,
    tips           TEXT,
    created_by     BIGINT REFERENCES users (id) ON DELETE SET NULL,
    status         VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_glossary_entries_category ON glossary_entries (category);
CREATE INDEX IF NOT EXISTS idx_glossary_entries_status ON glossary_entries (status);
CREATE INDEX IF NOT EXISTS idx_glossary_entries_official_name ON glossary_entries (official_name);

CREATE TABLE IF NOT EXISTS glossary_files (
    id          BIGSERIAL PRIMARY KEY,
    glossary_id BIGINT NOT NULL REFERENCES glossary_entries (id) ON DELETE CASCADE,
    name        VARCHAR(255) NOT NULL,
    path        VARCHAR(500) NOT NULL,
    mime_type   VARCHAR(100)
);

CREATE INDEX IF NOT EXISTS idx_glossary_files_glossary_id ON glossary_files (glossary_id);
