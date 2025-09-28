#!/usr/bin/env python3
"""
Create Keycloak Users for v2_quick_test.py

Creates the required users in systech realm:
- basic_user (password: systech@123)
- appadmin (password: systech@123)
- babu.systech (password: systech@123)

Author: Claude
"""

import requests
import json

# Configuration
KEYCLOAK_URL = "http://localhost:8090"
REALM = "systech"
ADMIN_USERNAME = "admin"
ADMIN_PASSWORD = "secret"

# Users to create (matching v2_quick_test.py expectations)
USERS_TO_CREATE = [
    {
        "username": "basic_user",
        "password": "systech@123",
        "email": "basic_user@systech.com",
        "firstName": "Basic",
        "lastName": "User"
    },
    {
        "username": "appadmin",
        "password": "systech@123",
        "email": "appadmin@systech.com",
        "firstName": "App",
        "lastName": "Admin"
    },
    {
        "username": "babu.systech",
        "password": "systech@123",
        "email": "babu@systech.com",
        "firstName": "Babu",
        "lastName": "Systech"
    }
]

def get_admin_token():
    """Get admin access token from Keycloak."""
    url = f"{KEYCLOAK_URL}/realms/master/protocol/openid-connect/token"

    data = {
        "grant_type": "password",
        "client_id": "admin-cli",
        "username": ADMIN_USERNAME,
        "password": ADMIN_PASSWORD
    }

    try:
        response = requests.post(url, data=data)
        if response.status_code == 200:
            return response.json().get("access_token")
        else:
            print(f"❌ Failed to get admin token: HTTP {response.status_code}")
            print(f"   Response: {response.text}")
            return None
    except Exception as e:
        print(f"❌ Error getting admin token: {e}")
        return None

def create_user(admin_token, user_data):
    """Create a single user in Keycloak realm."""
    url = f"{KEYCLOAK_URL}/admin/realms/{REALM}/users"

    headers = {
        "Authorization": f"Bearer {admin_token}",
        "Content-Type": "application/json"
    }

    payload = {
        "username": user_data["username"],
        "email": user_data["email"],
        "firstName": user_data["firstName"],
        "lastName": user_data["lastName"],
        "enabled": True,
        "emailVerified": True,
        "credentials": [{
            "type": "password",
            "value": user_data["password"],
            "temporary": False
        }]
    }

    try:
        response = requests.post(url, headers=headers, json=payload)
        if response.status_code == 201:
            print(f"✅ Created user: {user_data['username']}")
            return True
        elif response.status_code == 409:
            print(f"⚠️  User {user_data['username']} already exists")
            return True
        else:
            print(f"❌ Failed to create {user_data['username']}: HTTP {response.status_code}")
            print(f"   Response: {response.text}")
            return False
    except Exception as e:
        print(f"❌ Error creating {user_data['username']}: {e}")
        return False

def main():
    """Main execution."""
    print("🔧 Creating Keycloak Users for v2_quick_test.py")
    print("=" * 50)

    # Get admin token
    admin_token = get_admin_token()
    if not admin_token:
        print("❌ Could not authenticate with Keycloak admin")
        print("\nTroubleshooting:")
        print("1. Check that Keycloak is running: http://localhost:8090")
        print("2. Verify admin credentials: admin/secret")
        print("3. Check if systech realm exists")
        return

    print("✅ Admin token obtained")
    print()

    # Create users
    success_count = 0
    for user_data in USERS_TO_CREATE:
        if create_user(admin_token, user_data):
            success_count += 1

    print()
    print(f"📊 Created {success_count}/{len(USERS_TO_CREATE)} users")

    if success_count == len(USERS_TO_CREATE):
        print("🎉 All users created! You can now run:")
        print("   python3 scripts/v2_quick_test.py")
    else:
        print("⚠️  Some users failed to create. Check Keycloak admin console.")

if __name__ == "__main__":
    main()