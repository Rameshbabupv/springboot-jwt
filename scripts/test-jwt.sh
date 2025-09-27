#!/bin/bash
#
# JWT Authentication Test Script (Shell Version)
# Tests different user roles with Nexus API endpoints
#
# Usage: ./scripts/test-jwt.sh <username>
# Example: ./scripts/test-jwt.sh nexus-user

set -e

# Configuration
KEYCLOAK_URL="http://localhost:8090"
REALM="systech"
CLIENT_ID="systech-hrms-client"
API_BASE="http://localhost:8080"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    local status=$1
    local message=$2
    case $status in
        "success") echo -e "${GREEN}✅ $message${NC}" ;;
        "error") echo -e "${RED}❌ $message${NC}" ;;
        "warning") echo -e "${YELLOW}⚠️  $message${NC}" ;;
        "info") echo -e "${BLUE}ℹ️  $message${NC}" ;;
        "lock") echo -e "${YELLOW}🔒 $message${NC}" ;;
        "forbidden") echo -e "${RED}🚫 $message${NC}" ;;
    esac
}

# Function to get JWT token
get_jwt_token() {
    local username=$1
    local password=$2

    print_status "info" "Getting JWT token for user: $username"

    local response=$(curl -s -X POST "${KEYCLOAK_URL}/realms/${REALM}/protocol/openid-connect/token" \
        -H "Content-Type: application/x-www-form-urlencoded" \
        -d "grant_type=password&client_id=${CLIENT_ID}&username=${username}&password=${password}")

    # Check if response contains access_token
    if echo "$response" | grep -q "access_token"; then
        echo "$response" | python3 -c "import sys, json; print(json.load(sys.stdin)['access_token'])"
    else
        print_status "error" "Failed to get token: $response"
        return 1
    fi
}

# Function to decode JWT payload
decode_jwt() {
    local token=$1

    # Extract payload (middle part of JWT)
    local payload=$(echo "$token" | cut -d'.' -f2)

    # Add padding and decode
    local padded_payload="${payload}$(printf '%*s' $((4 - ${#payload} % 4)) | tr ' ' '=')"
    echo "$padded_payload" | base64 -d 2>/dev/null | python3 -c "import sys, json; print(json.dumps(json.load(sys.stdin), indent=2))" 2>/dev/null || echo "{}"
}

# Function to test API endpoint
test_endpoint() {
    local name=$1
    local url=$2
    local token=$3
    local needs_auth=$4

    if [ "$needs_auth" = "true" ]; then
        local response=$(curl -s -w "%{http_code}" -H "Authorization: Bearer $token" "$url")
    else
        local response=$(curl -s -w "%{http_code}" "$url")
    fi

    # Extract status code (last 3 characters)
    local status_code="${response: -3}"
    local body="${response%???}"

    case $status_code in
        200) print_status "success" "$(printf '%-8s' "$name") endpoint: $status_code - ${body:0:50}" ;;
        401) print_status "lock" "$(printf '%-8s' "$name") endpoint: $status_code - Unauthorized" ;;
        403) print_status "forbidden" "$(printf '%-8s' "$name") endpoint: $status_code - Forbidden" ;;
        *) print_status "warning" "$(printf '%-8s' "$name") endpoint: $status_code - ${body:0:50}" ;;
    esac
}

# Main function
main() {
    if [ $# -ne 1 ]; then
        echo "Usage: $0 <username>"
        echo "Available users: babu.systech (or other systech users)"
        exit 1
    fi

    local username=$1
    local password="systech@123"  # Default password for all test users

    # Validate username
    case $username in
        babu.systech|*) ;;
        *)
            print_status "error" "Unknown user: $username"
            echo "Available users: babu.systech (or other systech users)"
            exit 1
            ;;
    esac

    # Get JWT token
    local token=$(get_jwt_token "$username" "$password")
    if [ $? -ne 0 ]; then
        exit 1
    fi

    print_status "success" "Token obtained successfully"

    # Decode and show user info
    echo ""
    print_status "info" "User Information:"
    local payload=$(decode_jwt "$token")

    # Extract user details
    echo "$payload" | python3 -c "
import sys, json
try:
    data = json.load(sys.stdin)
    print(f\"   Name: {data.get('name', 'N/A')}\")
    print(f\"   Email: {data.get('email', 'N/A')}\")
    print(f\"   Username: {data.get('preferred_username', 'N/A')}\")

    realm_access = data.get('realm_access', {})
    roles = realm_access.get('roles', [])
    print(f\"   Roles: {', '.join(roles)}\")

    import datetime
    exp = data.get('exp')
    if exp:
        exp_time = datetime.datetime.fromtimestamp(exp)
        print(f\"   Expires: {exp_time.strftime('%Y-%m-%d %H:%M:%S')}\")
except:
    print('   Unable to decode user information')
"

    # Test API endpoints
    echo ""
    print_status "info" "Testing endpoints for user: $username"
    echo "=================================================="

    test_endpoint "Public" "${API_BASE}/api/hello/public" "$token" "false"
    test_endpoint "User" "${API_BASE}/api/user/hello" "$token" "true"
    test_endpoint "Manager" "${API_BASE}/api/manager/hello" "$token" "true"
    test_endpoint "Admin" "${API_BASE}/api/admin/hello" "$token" "true"

    echo ""
    print_status "info" "JWT Token (for manual testing):"
    echo "$token"
}

# Run main function
main "$@"