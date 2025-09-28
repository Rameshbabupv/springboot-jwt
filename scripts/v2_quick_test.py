#!/usr/bin/env python3
"""
Comprehensive JWT Authorization Matrix Testing Script

Tests 3 users across 4 security levels to validate group-based authorization.
Creates a complete authorization matrix showing which users can access which endpoints.

Author: Claude
Version: 2.0
"""

import requests
import json
import sys
from typing import Dict, Optional, Tuple
from datetime import datetime

# Configuration
KEYCLOAK_URL = "http://localhost:8090"
SPRING_BOOT_URL = "http://localhost:8080"
REALM = "systech"
CLIENT_ID = "systech-hrms-client"
PASSWORD = "systech@123"

# Test users and their expected groups
TEST_USERS = {
    "user": {
        "username": "basic_user",
        "expected_group": "basic-users",
        "description": "Basic User"
    },
    "app-admin": {
        "username": "appadmin",
        "expected_group": "app-admins",
        "description": "Application Admin"
    },
    "platform-admin": {
        "username": "babu.systech",
        "expected_group": "platform-admins",
        "description": "Platform Admin"
    }
}

# Test endpoints and their required access levels
ENDPOINTS = {
    "public": {
        "path": "/api/public/hello",
        "description": "Public Access",
        "auth_required": False
    },
    "user": {
        "path": "/api/user/hello",
        "description": "User Level",
        "auth_required": True
    },
    "manager": {
        "path": "/api/manager/hello",
        "description": "Manager Level",
        "auth_required": True
    },
    "admin": {
        "path": "/api/admin/hello",
        "description": "Admin Level",
        "auth_required": True
    }
}

# Expected authorization matrix (key matches TEST_USERS keys)
EXPECTED_RESULTS = {
    "user":          {"public": 200, "user": 200, "manager": 403, "admin": 403},
    "app-admin":     {"public": 200, "user": 200, "manager": 200, "admin": 403},
    "platform-admin": {"public": 200, "user": 200, "manager": 200, "admin": 200}
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

def test_endpoint(endpoint_key: str, token: Optional[str] = None) -> Tuple[int, str]:
    """
    Test a single endpoint with optional JWT token.

    Args:
        endpoint_key: Key identifying the endpoint to test
        token: Optional JWT token for authentication

    Returns:
        Tuple of (status_code, response_text)
    """
    endpoint = ENDPOINTS[endpoint_key]
    url = f"{SPRING_BOOT_URL}{endpoint['path']}"

    headers = {}
    if token and endpoint["auth_required"]:
        headers["Authorization"] = f"Bearer {token}"

    try:
        response = requests.get(url, headers=headers, timeout=10)
        return response.status_code, response.text[:100]  # Limit response text
    except Exception as e:
        return 0, f"Connection Error: {str(e)}"

def test_user_permissions(user_key: str) -> Dict[str, int]:
    """
    Test all endpoints for a single user.

    Args:
        user_key: Key identifying the user to test

    Returns:
        Dictionary mapping endpoint names to status codes
    """
    user_info = TEST_USERS[user_key]
    username = user_info["username"]

    print(f"🔐 Testing user: {Colors.CYAN}{username}{Colors.END} ({user_info['description']})")

    # Get JWT token
    token = get_jwt_token(username, PASSWORD)
    if not token:
        return {endpoint: 0 for endpoint in ENDPOINTS.keys()}

    results = {}

    # Test each endpoint
    for endpoint_key in ENDPOINTS.keys():
        endpoint = ENDPOINTS[endpoint_key]

        # For public endpoints, don't send token
        test_token = None if endpoint_key == "public" else token
        status_code, _ = test_endpoint(endpoint_key, test_token)
        results[endpoint_key] = status_code

        # Color code the result
        if status_code == 200:
            status_color = f"{Colors.GREEN}{status_code}{Colors.END}"
        elif status_code == 403:
            status_color = f"{Colors.RED}{status_code}{Colors.END}"
        elif status_code == 401:
            status_color = f"{Colors.YELLOW}{status_code}{Colors.END}"
        else:
            status_color = f"{Colors.RED}{status_code}{Colors.END}"

        print(f"  {endpoint['description']:.<15} {status_color}")

    print()
    return results

def display_results_matrix(all_results: Dict[str, Dict[str, int]]):
    """
    Display the complete authorization matrix in a tabular format.

    Args:
        all_results: Dictionary mapping user types to their test results
    """
    print(f"\n{Colors.BOLD}📊 AUTHORIZATION MATRIX RESULTS{Colors.END}")
    print("=" * 80)

    # Header
    print(f"{'User Type':<20} {'Public':<10} {'User':<10} {'Manager':<10} {'Admin':<10} {'Status'}")
    print("-" * 80)

    # Results for each user
    for user_key, results in all_results.items():
        user_info = TEST_USERS[user_key]
        expected = EXPECTED_RESULTS[user_key]

        # Format each result with color coding
        formatted_results = []
        all_match = True

        for endpoint in ["public", "user", "manager", "admin"]:
            actual = results.get(endpoint, 0)
            expected_code = expected[endpoint]

            if actual == expected_code:
                if actual == 200:
                    formatted_results.append(f"{Colors.GREEN}{actual}{Colors.END}")
                else:
                    formatted_results.append(f"{Colors.YELLOW}{actual}{Colors.END}")
            else:
                formatted_results.append(f"{Colors.RED}{actual}{Colors.END}")
                all_match = False

        # Overall status
        status = f"{Colors.GREEN}✅ PASS{Colors.END}" if all_match else f"{Colors.RED}❌ FAIL{Colors.END}"

        # Display row
        description = f"{user_info['description']} ({user_info['username']})"
        print(f"{description:<20} {formatted_results[0]:<20} {formatted_results[1]:<20} {formatted_results[2]:<20} {formatted_results[3]:<20} {status}")

    print("-" * 80)

    # Legend
    print(f"\n{Colors.BOLD}Legend:{Colors.END}")
    print(f"  {Colors.GREEN}200{Colors.END} = Success (Access Granted)")
    print(f"  {Colors.YELLOW}403{Colors.END} = Forbidden (Expected for insufficient permissions)")
    print(f"  {Colors.RED}401{Colors.END} = Unauthorized (Authentication failed)")
    print(f"  {Colors.RED}Other{Colors.END} = Error (Server error or connection issue)")

def display_expected_matrix():
    """Display the expected authorization matrix for reference."""
    print(f"\n{Colors.BOLD}📋 EXPECTED AUTHORIZATION MATRIX{Colors.END}")
    print("=" * 60)
    print(f"{'User Type':<20} {'Public':<10} {'User':<10} {'Manager':<10} {'Admin':<10}")
    print("-" * 60)

    for user_key, expected in EXPECTED_RESULTS.items():
        user_info = TEST_USERS[user_key]
        description = f"{user_info['description']}"
        print(f"{description:<20} {expected['public']:<10} {expected['user']:<10} {expected['manager']:<10} {expected['admin']:<10}")

    print("-" * 60)

def check_prerequisites():
    """Check if Keycloak and Spring Boot are running."""
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

    # Check Spring Boot
    try:
        spring_response = requests.get(f"{SPRING_BOOT_URL}/api/public/hello", timeout=5)
        if spring_response.status_code == 200:
            print(f"✅ Spring Boot is running on {SPRING_BOOT_URL}")
        else:
            print(f"❌ Spring Boot responded with status {spring_response.status_code}")
            return False
    except:
        print(f"❌ Spring Boot is not accessible at {SPRING_BOOT_URL}")
        return False

    print()
    return True

def main():
    """Main execution function."""
    print(f"{Colors.BOLD}🚀 JWT Authorization Matrix Testing v2.0{Colors.END}")
    print(f"Testing {len(TEST_USERS)} users across {len(ENDPOINTS)} endpoints")
    print(f"Timestamp: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print()

    # Check prerequisites
    if not check_prerequisites():
        print(f"{Colors.RED}❌ Prerequisites check failed. Please ensure Keycloak and Spring Boot are running.{Colors.END}")
        sys.exit(1)

    # Display expected results
    display_expected_matrix()

    # Test all users
    print(f"\n{Colors.BOLD}🧪 RUNNING TESTS...{Colors.END}")
    print("=" * 60)

    all_results = {}

    for user_key in TEST_USERS.keys():
        results = test_user_permissions(user_key)
        all_results[user_key] = results

    # Display comprehensive results matrix
    display_results_matrix(all_results)

    # Summary
    total_tests = len(TEST_USERS) * len(ENDPOINTS)
    successful_users = sum(1 for user_key, results in all_results.items()
                          if all(results.get(endpoint, 0) == EXPECTED_RESULTS[user_key][endpoint]
                                for endpoint in ENDPOINTS.keys()))

    print(f"\n{Colors.BOLD}📈 SUMMARY{Colors.END}")
    print(f"Total tests: {total_tests}")
    print(f"Users with correct permissions: {successful_users}/{len(TEST_USERS)}")

    if successful_users == len(TEST_USERS):
        print(f"{Colors.GREEN}🎉 All authorization tests PASSED! Group-based authentication is working correctly.{Colors.END}")
    else:
        print(f"{Colors.RED}⚠️  Some authorization tests FAILED. Please review the results above.{Colors.END}")

if __name__ == "__main__":
    main()