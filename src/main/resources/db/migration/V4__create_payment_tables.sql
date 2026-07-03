CREATE TABLE IF NOT EXISTS devises (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    code       VARCHAR(10) NOT NULL UNIQUE,
    taux_local DOUBLE PRECISION DEFAULT 22500.0
);

CREATE TABLE IF NOT EXISTS paiements (
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    reference     VARCHAR(255) NOT NULL,
    amount        VARCHAR(50) NOT NULL,
    devise        VARCHAR(10) NOT NULL DEFAULT 'USD',
    description   TEXT,
    type_payment  VARCHAR(50) NOT NULL DEFAULT 'MOBILE_MONEY',
    status        VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    date_created  DATE NOT NULL DEFAULT CURRENT_DATE,
    date_updated  DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE INDEX IF NOT EXISTS idx_paiements_user_id ON paiements (user_id);
CREATE INDEX IF NOT EXISTS idx_paiements_reference ON paiements (reference);
