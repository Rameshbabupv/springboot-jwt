#!/bin/bash
#
# Quick JWT Test Script
# Simple script to get token and test basic functionality
#

USERNAME=${1:-babu.systech}
PASSWORD="nexus123"

echo "🔐 Testing JWT for user: $USERNAME"

# Get token
echo "Getting token..."
TOKEN=$(curl -s -X POST "http://localhost:8090/realms/nexus-dev/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password&client_id=systech-hrms-client&username=$USERNAME&password=$PASSWORD" | \
  python3 -c "import sys, json; data=json.load(sys.stdin); print(data.get('access_token', 'ERROR'))" 2>/dev/null)

if [ "$TOKEN" = "ERROR" ] || [ -z "$TOKEN" ]; then
    echo "❌ Failed to get token"
    exit 1
fi

echo "✅ Token obtained"

# Test endpoints
echo ""
echo "🧪 Testing endpoints:"

echo -n "  Public:  "
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/hello/public

echo -n "  User:    "
curl -s -o /dev/null -w "%{http_code}" -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/user/hello

echo -n "  Manager: "
curl -s -o /dev/null -w "%{http_code}" -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/manager/hello

echo -n "  Admin:   "
curl -s -o /dev/null -w "%{http_code}" -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/admin/hello

echo ""
echo ""
echo "🎯 Token for manual testing:"
echo "$TOKEN"