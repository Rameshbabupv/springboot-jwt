#!/bin/bash
#
# Create Keycloak Users via Spring Boot API
# Uses admin token to create test users programmatically
#

set -e

KEYCLOAK_URL="http://localhost:8090"
REALM="systech"
CLIENT_ID="systech-hrms-client"
API_BASE="http://localhost:8080"

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}🔧 Creating Keycloak Users via Spring Boot API${NC}"

# Get admin token (assuming nexus-admin exists)
echo "Getting admin token..."
ADMIN_TOKEN=$(curl -s -X POST "${KEYCLOAK_URL}/realms/${REALM}/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password&client_id=${CLIENT_ID}&username=babu.systech&password=systech@123" | \
  python3 -c "import sys, json; data=json.load(sys.stdin); print(data.get('access_token', 'ERROR'))" 2>/dev/null)

if [ "$ADMIN_TOKEN" = "ERROR" ] || [ -z "$ADMIN_TOKEN" ]; then
    echo -e "${RED}❌ Could not get admin token. Make sure babu.systech user exists.${NC}"
    echo "You can create users manually in Keycloak first, or use the startup runner."
    exit 1
fi

echo -e "${GREEN}✅ Admin token obtained${NC}"

# Create test users via API
echo ""
echo "Creating test users via Spring Boot API..."

RESPONSE=$(curl -s -X POST "${API_BASE}/api/admin/users/create-test-users" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json")

echo "Response: $RESPONSE"

if echo "$RESPONSE" | grep -q '"success":true'; then
    echo -e "${GREEN}✅ Test users created successfully${NC}"
else
    echo -e "${RED}❌ Failed to create test users${NC}"
fi

echo ""
echo "Checking user existence..."

for user in babu.systech; do
    echo -n "  $user: "
    EXISTS=$(curl -s "${API_BASE}/api/admin/users/${user}/exists" \
      -H "Authorization: Bearer $ADMIN_TOKEN" | \
      python3 -c "import sys, json; data=json.load(sys.stdin); print(data.get('exists', False))" 2>/dev/null)

    if [ "$EXISTS" = "True" ]; then
        echo -e "${GREEN}✅ Exists${NC}"
    else
        echo -e "${RED}❌ Not found${NC}"
    fi
done

echo ""
echo -e "${BLUE}🎉 User creation process completed${NC}"