ALTER TABLE users
    ADD COLUMN IF NOT EXISTS premium_expires_at TIMESTAMP;

CREATE TABLE IF NOT EXISTS subscription_plans (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(150) NOT NULL,
    description     TEXT,
    price           VARCHAR(50) NOT NULL,
    devise          VARCHAR(10) NOT NULL DEFAULT 'USD',
    duration_days   INT NOT NULL,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_subscriptions (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    plan_id             BIGINT NOT NULL REFERENCES subscription_plans (id),
    payment_reference   VARCHAR(255),
    status              VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    starts_at           TIMESTAMP NOT NULL,
    expires_at          TIMESTAMP NOT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_user_subscriptions_user_id ON user_subscriptions (user_id);
CREATE INDEX IF NOT EXISTS idx_user_subscriptions_status ON user_subscriptions (user_id, status);

ALTER TABLE paiements
    ADD COLUMN IF NOT EXISTS subscription_plan_id BIGINT REFERENCES subscription_plans (id) ON DELETE SET NULL;

CREATE TABLE IF NOT EXISTS mechanics (
    id                  BIGSERIAL PRIMARY KEY,
    garage_id           BIGINT REFERENCES garages (id) ON DELETE SET NULL,
    full_name           VARCHAR(255) NOT NULL,
    phone               VARCHAR(50) NOT NULL,
    city                VARCHAR(100),
    latitude            DOUBLE PRECISION,
    longitude           DOUBLE PRECISION,
    is_night_available  BOOLEAN NOT NULL DEFAULT FALSE,
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_mechanics_night_available ON mechanics (is_night_available, is_active);

CREATE TABLE IF NOT EXISTS mechanic_contact_requests (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    mechanic_id     BIGINT NOT NULL REFERENCES mechanics (id) ON DELETE CASCADE,
    message         TEXT,
    status          VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_mechanic_contacts_user_id ON mechanic_contact_requests (user_id);

INSERT INTO subscription_plans (name, description, price, devise, duration_days)
SELECT 'Premium Mensuel', 'Accès aux garages et contact mécaniciens de nuit', '9.99', 'USD', 30
WHERE NOT EXISTS (SELECT 1 FROM subscription_plans WHERE name = 'Premium Mensuel');

INSERT INTO subscription_plans (name, description, price, devise, duration_days)
SELECT 'Premium Annuel', 'Accès premium 12 mois', '99.99', 'USD', 365
WHERE NOT EXISTS (SELECT 1 FROM subscription_plans WHERE name = 'Premium Annuel');
