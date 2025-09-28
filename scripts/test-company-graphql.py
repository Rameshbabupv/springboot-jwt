#!/usr/bin/env python3
"""
Test script for Company GraphQL queries with Keycloak authentication.

This script:
1. Gets JWT token from Keycloak for babu.systech user
2. Makes GraphQL query to get all companies
3. Displays the results with proper formatting

Requirements:
- Keycloak running on localhost:8090
- Spring Boot app running on localhost:8080
- babu.systech user with admin privileges in Keycloak

Author: Backend Developer
Version: 1.0
"""

import requests
import json
import sys
from typing import Dict, Any, Optional

# Configuration
KEYCLOAK_BASE_URL = "http://localhost:8090"
KEYCLOAK_REALM = "systech"
KEYCLOAK_CLIENT_ID = "systech-hrms-client"
SPRING_BOOT_URL = "http://localhost:8080"
GRAPHQL_ENDPOINT = f"{SPRING_BOOT_URL}/graphql"

# User credentials for babu.systech (admin user)
USERNAME = "babu.systech"
PASSWORD = "systech@123"  # Default password from Keycloak setup


class KeycloakAuthError(Exception):
    """Custom exception for Keycloak authentication errors."""
    pass


class GraphQLError(Exception):
    """Custom exception for GraphQL errors."""
    pass


def get_keycloak_token(username: str, password: str) -> str:
    """
    Get JWT access token from Keycloak for the specified user.

    Args:
        username: Keycloak username
        password: User password

    Returns:
        JWT access token string

    Raises:
        KeycloakAuthError: If authentication fails
    """
    token_url = f"{KEYCLOAK_BASE_URL}/realms/{KEYCLOAK_REALM}/protocol/openid-connect/token"

    # OAuth2 password grant request
    token_data = {
        "grant_type": "password",
        "client_id": KEYCLOAK_CLIENT_ID,
        "username": username,
        "password": password
    }

    headers = {
        "Content-Type": "application/x-www-form-urlencoded"
    }

    print(f"🔐 Getting JWT token for user: {username}")
    print(f"📡 Keycloak URL: {token_url}")

    try:
        response = requests.post(token_url, data=token_data, headers=headers, timeout=10)

        if response.status_code == 200:
            token_response = response.json()
            access_token = token_response.get("access_token")
            if access_token:
                print(f"✅ Successfully obtained JWT token")
                print(f"🕒 Token expires in: {token_response.get('expires_in', 'unknown')} seconds")
                return access_token
            else:
                raise KeycloakAuthError("No access_token in response")
        else:
            error_detail = ""
            try:
                error_data = response.json()
                error_detail = f" - {error_data.get('error_description', error_data.get('error', 'Unknown error'))}"
            except:
                error_detail = f" - {response.text}"

            raise KeycloakAuthError(f"Authentication failed (HTTP {response.status_code}){error_detail}")

    except requests.RequestException as e:
        raise KeycloakAuthError(f"Failed to connect to Keycloak: {e}")


def execute_graphql_query(token: str, query: str, variables: Optional[Dict[str, Any]] = None) -> Dict[str, Any]:
    """
    Execute a GraphQL query against the Spring Boot application.

    Args:
        token: JWT access token
        query: GraphQL query string
        variables: Optional query variables

    Returns:
        GraphQL response data

    Raises:
        GraphQLError: If the query fails
    """
    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {token}"
    }

    payload = {"query": query}
    if variables:
        payload["variables"] = variables

    print(f"📊 Executing GraphQL query...")
    print(f"🎯 Endpoint: {GRAPHQL_ENDPOINT}")

    try:
        response = requests.post(GRAPHQL_ENDPOINT, json=payload, headers=headers, timeout=10)

        if response.status_code == 200:
            response_data = response.json()

            # Check for GraphQL errors
            if "errors" in response_data:
                errors = response_data["errors"]
                error_messages = [error.get("message", str(error)) for error in errors]
                raise GraphQLError(f"GraphQL errors: {', '.join(error_messages)}")

            print(f"✅ GraphQL query executed successfully")
            return response_data

        else:
            error_detail = f"HTTP {response.status_code}"
            try:
                error_data = response.json()
                if "errors" in error_data:
                    error_messages = [error.get("message", str(error)) for error in error_data["errors"]]
                    error_detail += f" - {', '.join(error_messages)}"
                else:
                    error_detail += f" - {error_data}"
            except:
                error_detail += f" - {response.text}"

            raise GraphQLError(f"GraphQL request failed: {error_detail}")

    except requests.RequestException as e:
        raise GraphQLError(f"Failed to connect to GraphQL endpoint: {e}")


def format_company_data(companies: list) -> None:
    """
    Pretty print company data in a readable format.

    Args:
        companies: List of company objects from GraphQL response
    """
    if not companies:
        print("📭 No companies found")
        return

    print(f"\n🏢 Found {len(companies)} company(ies):")
    print("=" * 80)

    for i, company in enumerate(companies, 1):
        print(f"\n📋 Company #{i}:")
        print(f"  🆔 ID: {company.get('id', 'N/A')}")
        print(f"  🏷️  Code: {company.get('companyCode', 'N/A')}")
        print(f"  🏛️  Name: {company.get('companyName', 'N/A')}")
        print(f"  📧 Email: {company.get('primaryEmail', 'N/A')}")
        print(f"  🌍 Country: {company.get('country', 'N/A')}")
        print(f"  📊 Status: {company.get('companyStatus', 'N/A')}")
        print(f"  📅 Created: {company.get('createdAt', 'N/A')}")
        print(f"  🔄 Modified: {company.get('modifiedAt', 'N/A')}")


def main():
    """Main function to execute the test script."""
    print("🚀 Starting Company GraphQL Test Script")
    print("=" * 60)

    try:
        # Step 1: Get JWT token from Keycloak
        token = get_keycloak_token(USERNAME, PASSWORD)

        # Step 2: Define GraphQL query for companies
        companies_query = """
        query GetAllCompanies {
            companies {
                id
                companyCode
                companyName
                companyShortName
                primaryEmail
                registeredCountryId
                country
                companyType
                companyStatus
                createdAt
                modifiedAt
                financialYearStartMonth
                financialYearEndMonth
                defaultCurrencyCode
                defaultLanguage
            }
        }
        """

        # Step 3: Execute GraphQL query
        response_data = execute_graphql_query(token, companies_query)

        # Step 4: Display results
        if "data" in response_data and "companies" in response_data["data"]:
            companies = response_data["data"]["companies"]
            format_company_data(companies)
        else:
            print("❌ Unexpected response format")
            print(f"📄 Raw response: {json.dumps(response_data, indent=2)}")

        print("\n🎉 Test completed successfully!")

    except KeycloakAuthError as e:
        print(f"❌ Keycloak Authentication Error: {e}")
        print("💡 Troubleshooting:")
        print("   - Check if Keycloak is running on localhost:8090")
        print("   - Verify babu.systech user exists with correct password")
        print("   - Ensure user has admin privileges")
        sys.exit(1)

    except GraphQLError as e:
        print(f"❌ GraphQL Error: {e}")
        print("💡 Troubleshooting:")
        print("   - Check if Spring Boot app is running on localhost:8080")
        print("   - Verify JWT token has admin role (app-admins or platform-admins)")
        print("   - Check application logs for detailed error messages")
        sys.exit(1)

    except Exception as e:
        print(f"❌ Unexpected Error: {e}")
        sys.exit(1)


if __name__ == "__main__":
    main()