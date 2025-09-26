#!/usr/bin/env python3
"""
JWT Authentication Test Script for Nexus Application

This script tests JWT authentication with different user roles:
- nexus-user: Basic user access
- nexus-manager: Manager level access
- nexus-admin: Administrator access

Usage:
    python3 scripts/test-jwt.py <username>

Examples:
    python3 scripts/test-jwt.py nexus-user
    python3 scripts/test-jwt.py nexus-manager
    python3 scripts/test-jwt.py nexus-admin
"""

import sys
import json
import requests
import base64
from datetime import datetime

# Configuration
KEYCLOAK_URL = "http://localhost:8090"
REALM = "nexus-dev"
CLIENT_ID = "nexus-web-app"
API_BASE = "http://localhost:8080"

# User credentials (as configured in Keycloak)
USERS = {
    "nexus-user": "nexus123",
    "nexus-manager": "nexus123",
    "nexus-admin": "nexus123"
}

def get_jwt_token(username, password):
    """Get JWT token from Keycloak"""
    token_url = f"{KEYCLOAK_URL}/realms/{REALM}/protocol/openid-connect/token"

    data = {
        "grant_type": "password",
        "client_id": CLIENT_ID,
        "username": username,
        "password": password
    }

    try:
        response = requests.post(token_url, data=data)
        response.raise_for_status()
        return response.json()
    except requests.RequestException as e:
        print(f"❌ Failed to get token: {e}")
        return None

def decode_jwt_payload(token):
    """Decode JWT payload to show user info"""
    try:
        # Split JWT and get payload (middle part)
        parts = token.split('.')
        payload = parts[1]

        # Add padding if needed
        payload += '=' * (4 - len(payload) % 4)

        # Decode base64
        decoded_bytes = base64.b64decode(payload)
        payload_json = json.loads(decoded_bytes)

        return payload_json
    except Exception as e:
        print(f"❌ Failed to decode JWT: {e}")
        return None

def test_endpoints(token, username):
    """Test different API endpoints with the token"""
    headers = {"Authorization": f"Bearer {token}"}

    endpoints = [
        ("Public", f"{API_BASE}/api/hello/public", "GET", False),
        ("User", f"{API_BASE}/api/user/hello", "GET", True),
        ("Manager", f"{API_BASE}/api/manager/hello", "GET", True),
        ("Admin", f"{API_BASE}/api/admin/hello", "GET", True),
    ]

    print(f"\n🧪 Testing endpoints for user: {username}")
    print("=" * 50)

    for name, url, method, needs_auth in endpoints:
        try:
            if needs_auth:
                response = requests.get(url, headers=headers)
            else:
                response = requests.get(url)

            status = response.status_code

            if status == 200:
                print(f"✅ {name:8} endpoint: {status} - {response.text[:50]}")
            elif status == 401:
                print(f"🔒 {name:8} endpoint: {status} - Unauthorized")
            elif status == 403:
                print(f"🚫 {name:8} endpoint: {status} - Forbidden")
            else:
                print(f"❓ {name:8} endpoint: {status} - {response.text[:50]}")

        except requests.RequestException as e:
            print(f"❌ {name:8} endpoint: Error - {e}")

def main():
    if len(sys.argv) != 2:
        print("Usage: python3 scripts/test-jwt.py <username>")
        print("Available users: nexus-user, nexus-manager, nexus-admin")
        sys.exit(1)

    username = sys.argv[1]

    if username not in USERS:
        print(f"❌ Unknown user: {username}")
        print(f"Available users: {', '.join(USERS.keys())}")
        sys.exit(1)

    password = USERS[username]

    print(f"🔐 Getting JWT token for user: {username}")

    # Get token
    token_response = get_jwt_token(username, password)
    if not token_response:
        sys.exit(1)

    access_token = token_response.get("access_token")
    expires_in = token_response.get("expires_in")

    print(f"✅ Token obtained successfully (expires in {expires_in}s)")

    # Decode and show token info
    payload = decode_jwt_payload(access_token)
    if payload:
        print(f"\n📋 User Information:")
        print(f"   Name: {payload.get('name', 'N/A')}")
        print(f"   Email: {payload.get('email', 'N/A')}")
        print(f"   Username: {payload.get('preferred_username', 'N/A')}")

        realm_access = payload.get('realm_access', {})
        roles = realm_access.get('roles', [])
        print(f"   Roles: {', '.join(roles)}")

        exp = payload.get('exp')
        if exp:
            exp_time = datetime.fromtimestamp(exp)
            print(f"   Expires: {exp_time.strftime('%Y-%m-%d %H:%M:%S')}")

    # Test API endpoints
    test_endpoints(access_token, username)

    print(f"\n🎯 JWT Token (for manual testing):")
    print(f"{access_token}")

if __name__ == "__main__":
    main()