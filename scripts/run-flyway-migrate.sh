#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
MIGRATIONS_DIR="${ROOT_DIR}/src/main/resources/db/migration"

FLYWAY_URL="${FLYWAY_URL:-${DATABASE_URL:-}}"
FLYWAY_USER="${FLYWAY_USER:-${USERNAME:-postgres}}"
FLYWAY_PASSWORD="${FLYWAY_PASSWORD:-${PASSWORD:-}}"

if [[ -z "${FLYWAY_URL}" ]]; then
  echo "Erreur: définissez FLYWAY_URL (jdbc:postgresql://host:5432/db) ou DATABASE_URL." >&2
  exit 1
fi

if [[ "${FLYWAY_URL}" == r2dbc:* ]]; then
  FLYWAY_URL="jdbc:${FLYWAY_URL#r2dbc:}"
fi

echo "Migration Flyway vers ${FLYWAY_URL} ..."

docker run --rm \
  -v "${MIGRATIONS_DIR}:/flyway/sql" \
  flyway/flyway:11-alpine \
  -url="${FLYWAY_URL}" \
  -user="${FLYWAY_USER}" \
  -password="${FLYWAY_PASSWORD}" \
  -baselineOnMigrate=true \
  migrate

echo "Migration terminée."
