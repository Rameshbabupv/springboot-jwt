#!/usr/bin/env python3
"""
Script to create 3 test users in H2 database via GraphQL mutations.
"""

import requests
import json

# Configuration
KEYCLOAK_URL = "http://localhost:8090"
SPRING_BOOT_URL = "http://localhost:8080"
GRAPHQL_ENDPOINT = f"{SPRING_BOOT_URL}/graphql"
REALM = "systech"
CLIENT_ID = "systech-hrms-client"
PASSWORD = "systech@123"

def get_jwt_token(username: str, password: str) -> str:
    """Get JWT token from Keycloak."""
    token_url = f"{KEYCLOAK_URL}/realms/{REALM}/protocol/openid-connect/token"

    data = {
        "grant_type": "password",
        "client_id": CLIENT_ID,
        "username": username,
        "password": password
    }

    response = requests.post(token_url, data=data, timeout=10)
    if response.status_code == 200:
        return response.json()["access_token"]
    else:
        raise Exception(f"Failed to get token: {response.status_code}")

def create_user_via_graphql(token: str, username: str, email: str, first_name: str, last_name: str):
    """Create user via GraphQL mutation."""
    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {token}"
    }

    mutation = """
    mutation {
        createUser(input: {username: "%s", email: "%s", firstName: "%s", lastName: "%s"}) {
            id
            username
            email
            firstName
            lastName
        }
    }
    """ % (username, email, first_name, last_name)

    payload = {
        "query": mutation
    }

    print(f"Creating user: {username}")
    print(f"Payload: {json.dumps(payload, indent=2)}")

    response = requests.post(GRAPHQL_ENDPOINT, headers=headers, json=payload, timeout=10)

    print(f"Response Status: {response.status_code}")
    print(f"Response Body: {response.text}")
    print("-" * 50)

    return response.json() if response.status_code == 200 else None

def main():
    print("🔐 Getting platform admin token...")
    token = get_jwt_token("babu.systech", PASSWORD)
    print(f"✅ Token obtained (length: {len(token)})")

    users_to_create = [
        ("basic_user", "basic.user@systech.com", "Basic", "User"),
        ("appadmin", "app.admin@systech.com", "App", "Admin"),
        ("babu.systech", "babu@systech.com", "Babu", "Systech")
    ]

    print(f"\n👥 Creating {len(users_to_create)} test users...")

    for username, email, first_name, last_name in users_to_create:
        try:
            result = create_user_via_graphql(token, username, email, first_name, last_name)
            if result:
                print(f"✅ User {username} created successfully")
            else:
                print(f"❌ Failed to create user {username}")
        except Exception as e:
            print(f"❌ Error creating user {username}: {str(e)}")

    print("\n🏁 User creation complete!")

if __name__ == "__main__":
    main()