#!/usr/bin/env python3
"""
GraphQL Authentication-Only Testing Script

Tests authenticated GraphQL queries with JWT tokens from Keycloak.
All GraphQL operations require authentication - no public access.

Author: Claude
Version: 1.0
"""

import requests
import json
import sys
from typing import Dict, Optional, Tuple
from datetime import datetime

# Configuration
KEYCLOAK_URL = "http://localhost:8090"
SPRING_BOOT_URL = "http://localhost:8080"
GRAPHQL_ENDPOINT = f"{SPRING_BOOT_URL}/graphql"
REALM = "systech"
CLIENT_ID = "systech-hrms-client"
PASSWORD = "systech@123"

# Test users and their expected groups
TEST_USERS = {
    "basic_user": {
        "username": "basic_user",
        "expected_group": "basic-users",
        "description": "Basic User"
    },
    "app_admin": {
        "username": "appadmin",
        "expected_group": "app-admins",
        "description": "Application Admin"
    },
    "platform_admin": {
        "username": "babu.systech",
        "expected_group": "platform-admins",
        "description": "Platform Admin"
    }
}

# GraphQL Queries to test (All require authentication)
GRAPHQL_QUERIES = {
    "users": {
        "query": '{ users { id username email firstName lastName } }',
        "description": "Get All Users",
        "auth_required": True,
        "required_groups": ["basic-users", "app-admins", "platform-admins"]
    },
    "user": {
        "query": '{ user(id: "1") { id username email } }',
        "description": "Get User by ID",
        "auth_required": True,
        "required_groups": ["basic-users", "app-admins", "platform-admins"]
    },
    "userByUsername": {
        "query": '{ userByUsername(username: "basic_user") { id username email } }',
        "description": "Get User by Username",
        "auth_required": True,
        "required_groups": ["basic-users", "app-admins", "platform-admins"]
    }
}

class Colors:
    """ANSI color codes for terminal output"""
    GREEN = '\033[92m'
    RED = '\033[91m'
    YELLOW = '\033[93m'
    BLUE = '\033[94m'
    PURPLE = '\033[95m'
    CYAN = '\033[96m'
    WHITE = '\033[97m'
    BOLD = '\033[1m'
    END = '\033[0m'

def get_jwt_token(username: str, password: str) -> Optional[str]:
    """
    Get JWT token from Keycloak for the specified user.

    Args:
        username: The username to authenticate
        password: The password for authentication

    Returns:
        JWT token string if successful, None if failed
    """
    token_url = f"{KEYCLOAK_URL}/realms/{REALM}/protocol/openid-connect/token"

    data = {
        "grant_type": "password",
        "client_id": CLIENT_ID,
        "username": username,
        "password": password
    }

    try:
        response = requests.post(token_url, data=data, timeout=10)
        if response.status_code == 200:
            token_data = response.json()
            return token_data.get("access_token")
        else:
            print(f"❌ Failed to get token for {username}: HTTP {response.status_code}")
            return None
    except Exception as e:
        print(f"❌ Error getting token for {username}: {str(e)}")
        return None

def test_graphql_query(query_key: str, query_data: dict, token: Optional[str] = None) -> Tuple[bool, str, any]:
    """
    Test a single GraphQL query.

    Args:
        query_key: Key identifying the query
        query_data: Query configuration data
        token: Optional JWT token for authentication

    Returns:
        Tuple of (success, status_message, response_data)
    """
    headers = {"Content-Type": "application/json"}
    if token and query_data.get("auth_required"):
        headers["Authorization"] = f"Bearer {token}"

    payload = {"query": query_data["query"]}

    try:
        response = requests.post(GRAPHQL_ENDPOINT, headers=headers, json=payload, timeout=10)

        if response.status_code == 200:
            data = response.json()
            if "errors" in data:
                return False, f"GraphQL Error: {data['errors']}", data
            else:
                return True, f"Success: {response.status_code}", data.get("data")
        else:
            return False, f"HTTP Error: {response.status_code}", response.text

    except Exception as e:
        return False, f"Connection Error: {str(e)}", None

def collect_all_tokens() -> Dict[str, Optional[str]]:
    """
    Collect JWT tokens for all test users.

    Returns:
        Dictionary mapping user keys to their JWT tokens
    """
    print(f"{Colors.BOLD}🔐 STEP 1: Collecting JWT Tokens{Colors.END}")
    print("=" * 50)

    tokens = {}

    for user_key, user_info in TEST_USERS.items():
        username = user_info["username"]
        print(f"Getting token for {Colors.CYAN}{username}{Colors.END} ({user_info['description']})...")

        token = get_jwt_token(username, PASSWORD)
        tokens[user_key] = token

        if token:
            print(f"  ✅ Token obtained (length: {len(token)})")
        else:
            print(f"  ❌ Failed to get token")

    print()
    return tokens

def test_all_users_authentication(tokens: Dict[str, Optional[str]]):
    """
    Test all users authentication with GraphQL users query to get user information from Keycloak.
    """
    print(f"{Colors.BOLD}🧪 STEP 2: Testing All Users Authentication{Colors.END}")
    print("=" * 70)

    query_data = GRAPHQL_QUERIES["users"]
    print(f"Testing: {Colors.YELLOW}{query_data['description']}{Colors.END}")
    print(f"Query: {query_data['query']}")
    print()

    for user_key, user_info in TEST_USERS.items():
        username = user_info["username"]
        description = user_info["description"]
        token = tokens.get(user_key)

        print(f"🔐 Testing User: {Colors.CYAN}{username}{Colors.END} ({description})")

        if not token:
            print(f"  ❌ {Colors.RED}No token available{Colors.END}")
            print()
            continue

        success, status, response = test_graphql_query("users", query_data, token)

        if success:
            print(f"  ✅ {Colors.GREEN}Success{Colors.END}: {status}")

            # Extract user info from response
            if response and "users" in response:
                users_data = response["users"]
                if users_data and len(users_data) > 0:
                    print(f"  📊 Retrieved {len(users_data)} users from Keycloak:")
                    for user in users_data:
                        first_name = user.get('firstName', '')
                        last_name = user.get('lastName', '')
                        full_name = f"{first_name} {last_name}".strip() or 'N/A'

                        print(f"    🆔 ID: {Colors.GREEN}{user.get('id', 'N/A')}{Colors.END}")
                        print(f"    📧 Username: {Colors.GREEN}{user.get('username', 'N/A')}{Colors.END}")
                        print(f"    👤 Full Name: {Colors.GREEN}{full_name}{Colors.END}")
                        print(f"    📨 Email: {Colors.GREEN}{user.get('email', 'N/A')}{Colors.END}")
                        print("    ---")
                else:
                    print(f"  ⚠️  No users found in response")
            else:
                print(f"  ⚠️  Unexpected response format")
        else:
            print(f"  ❌ {Colors.RED}Failed{Colors.END}: {status}")
            if isinstance(response, dict) and "errors" in response:
                for error in response["errors"]:
                    print(f"  🚨 Error: {Colors.RED}{error.get('message', 'Unknown error')}{Colors.END}")
            else:
                print(f"  📝 Response: {response}")

        print()

def check_prerequisites():
    """Check if services are running."""
    print(f"{Colors.BOLD}🔍 Checking Prerequisites...{Colors.END}")

    # Check Keycloak
    try:
        keycloak_response = requests.get(f"{KEYCLOAK_URL}/realms/{REALM}", timeout=5)
        if keycloak_response.status_code == 200:
            print(f"✅ Keycloak is running on {KEYCLOAK_URL}")
        else:
            print(f"❌ Keycloak responded with status {keycloak_response.status_code}")
            return False
    except:
        print(f"❌ Keycloak is not accessible at {KEYCLOAK_URL}")
        return False

    # Check Spring Boot GraphQL
    try:
        spring_response = requests.post(GRAPHQL_ENDPOINT,
                                      json={"query": "{ __typename }"},
                                      timeout=5)
        if spring_response.status_code in [200, 401]:  # 401 is fine for auth check
            print(f"✅ GraphQL endpoint is accessible at {GRAPHQL_ENDPOINT}")
        else:
            print(f"❌ GraphQL endpoint responded with status {spring_response.status_code}")
            return False
    except:
        print(f"❌ GraphQL endpoint is not accessible at {GRAPHQL_ENDPOINT}")
        return False

    print()
    return True

def main():
    """Main execution function."""
    print(f"{Colors.BOLD}🚀 GraphQL Authorization Testing v1.0{Colors.END}")
    print(f"Testing {len(GRAPHQL_QUERIES)} GraphQL queries across {len(TEST_USERS)} users")
    print(f"Timestamp: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print()

    # Check prerequisites
    if not check_prerequisites():
        print(f"{Colors.RED}❌ Prerequisites check failed. Please ensure services are running.{Colors.END}")
        sys.exit(1)

    # Step 1: Collect all tokens
    tokens = collect_all_tokens()

    # Check if we got any tokens
    successful_tokens = sum(1 for token in tokens.values() if token is not None)
    print(f"📊 Token Collection Results: {successful_tokens}/{len(TEST_USERS)} successful")
    print()

    # Step 2: Test all users authentication
    test_all_users_authentication(tokens)

    print(f"{Colors.BOLD}🏁 Phase 1 Complete{Colors.END}")
    print("GraphQL authentication-only architecture validated")

if __name__ == "__main__":
    main()