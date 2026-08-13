#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8095}"
VERSION="${VERSION:-v1}"
PASS=0
FAIL=0

check() {
  local name="$1"
  local expected="$2"
  local method="$3"
  local path="$4"
  local body="${5:-}"
  local url="${BASE_URL}/api/${VERSION}${path}"

  if [[ -n "$body" ]]; then
    status=$(curl -s -o /tmp/api_test_body.json -w "%{http_code}" -X "$method" \
      -H "Content-Type: application/json" \
      -d "$body" "$url")
  else
    status=$(curl -s -o /tmp/api_test_body.json -w "%{http_code}" -X "$method" "$url")
  fi

  if [[ "$status" == "$expected" ]]; then
    echo "OK  [$status] $name"
    PASS=$((PASS + 1))
  else
    echo "FAIL [$status expected $expected] $name"
    echo "    URL: $url"
    head -c 300 /tmp/api_test_body.json 2>/dev/null || true
    echo
    FAIL=$((FAIL + 1))
  fi
}

echo "=== Test nouvelles APIs (mode test public) ==="
echo "Base: $BASE_URL"

# Abonnements (public)
check "Plans abonnement" "200" "GET" "/public/subscription-plans"

# Panier
check "Panier actif" "200" "GET" "/protected/cart/active"
check "Résumé panier (vide attendu 400)" "400" "GET" "/protected/cart/summary?deviseId=1"

# Favoris
check "Liste favoris" "200" "GET" "/protected/favorites"

# Signalements
check "Compteur signalements CAR" "200" "GET" "/protected/listing-reports/CAR/1/count"

# Garages (premium bypass en mode test)
check "Liste garages premium" "200" "GET" "/protected/garages/browse"

# Abonnement status
check "Statut premium utilisateur" "200" "GET" "/protected/subscriptions/status"

# Mécaniciens (premium bypass)
check "Mécaniciens nuit" "200" "GET" "/protected/mechanics/night-available"

# Mot de passe oublié (public natif - OTP invalide attendu)
check "Reset password public (OTP invalide)" "400" "PUT" "/public/reset/password" \
  '{"identifier":"test@example.com","code":"000000","newPassword":"secret123"}'

echo
echo "=== Résultat: $PASS OK, $FAIL FAIL ==="
[[ "$FAIL" -eq 0 ]]
