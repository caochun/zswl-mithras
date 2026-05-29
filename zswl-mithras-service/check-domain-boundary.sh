#!/bin/bash
# Domain boundary check - measures cross-domain coupling for all facades
# Violations should decrease over time as code migrates to Facades

SVC="src/main/java/cn/zswltech/mithras/service"
cd "$(dirname "$0")"

echo "=== Domain Boundary Check ==="
echo ""

check_domain() {
  local domain="$1"
  local service_pattern="$2"
  local exclude_pattern="$3"

  local svc_count=$(grep -rl "$service_pattern" "$SVC" --include="*.java" \
    | grep -v "$exclude_pattern" | grep -v "/facade/" | wc -l)
  echo "  [$domain] External files importing domain services: $svc_count"
}

check_domain "fund" \
  "import.*service\.service\.fund\.\|import.*service\.fund\.direct\.service\." \
  "/fund/"

check_domain "contract" \
  "import.*service\.service\.contract\." \
  "/contract/"

check_domain "client" \
  "import.*service\.service\.client\." \
  "/client/"

check_domain "payment" \
  "import.*service\.service\.payment\." \
  "/payment/"

echo ""
echo "Target: all 0 (all external access via Facades)"
echo "Facades available in: service/facade/{fund,contract,client,payment}/"
