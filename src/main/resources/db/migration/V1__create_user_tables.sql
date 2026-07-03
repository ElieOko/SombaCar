CREATE TABLE IF NOT EXISTS type_accounts (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    id           BIGSERIAL PRIMARY KEY,
    password     VARCHAR(255),
    email        VARCHAR(255),
    username     VARCHAR(255),
    first_name   VARCHAR(100) NOT NULL,
    last_name    VARCHAR(100) NOT NULL,
    city         VARCHAR(100) NOT NULL,
    full_name    VARCHAR(255) NOT NULL,
    from_service VARCHAR(100),
    is_premium   BOOLEAN NOT NULL DEFAULT FALSE,
    is_certified BOOLEAN NOT NULL DEFAULT FALSE,
    is_lock      BOOLEAN NOT NULL DEFAULT FALSE,
    is_valid     BOOLEAN NOT NULL DEFAULT FALSE,
    phone        VARCHAR(50),
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users (email);
CREATE INDEX IF NOT EXISTS idx_users_phone ON users (phone);

CREATE TABLE IF NOT EXISTS accounts (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    type_account_id BIGINT NOT NULL REFERENCES type_accounts (id)
);

CREATE INDEX IF NOT EXISTS idx_accounts_type_account_id ON accounts (type_account_id);

CREATE TABLE IF NOT EXISTS account_users (
    id         BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES accounts (id) ON DELETE CASCADE,
    user_id    BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    UNIQUE (account_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_account_users_user_id ON account_users (user_id);
CREATE INDEX IF NOT EXISTS idx_account_users_account_id ON account_users (account_id);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    expires_at   TIMESTAMP NOT NULL,
    hashed_token VARCHAR(512) NOT NULL,
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON refresh_tokens (user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_hashed_token ON refresh_tokens (hashed_token);
