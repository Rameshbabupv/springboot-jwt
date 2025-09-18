# JWT Authentication Testing Guide

This document provides comprehensive instructions for testing the JWT authentication system in the Nexus Spring Boot application.

## Table of Contents

- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Testing Environment Setup](#testing-environment-setup)
- [Quick Testing](#quick-testing)
- [Comprehensive Testing](#comprehensive-testing)
- [Manual Testing](#manual-testing)
- [Troubleshooting](#troubleshooting)
- [Security Validation](#security-validation)

## Overview

The Nexus application implements JWT (JSON Web Token) authentication using:
- **Spring Security OAuth2 Resource Server** for JWT validation
- **Keycloak** as the identity provider
- **Role-Based Access Control (RBAC)** with three levels:
  - `nexus-user` - Basic user access
  - `nexus-manager` - Manager level access
  - `nexus-admin` - Administrative access

## Prerequisites

### Required Software
- Java 17+
- Maven 3.8+
- curl (for API testing)
- Python 3 (for JWT token parsing in scripts)
- Podman or Docker (for Keycloak)

### Application Profiles
- **dev** - Full JWT authentication with Keycloak integration
- **dev-no-auth** - Development mode without authentication (for testing)

## Testing Environment Setup

### 1. Start the Application

#### Option A: With JWT Authentication (Recommended)
```bash
# Terminal 1: Start the application
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Option B: Without Authentication (For Development)
```bash
# Alternative: No authentication mode
mvn spring-boot:run -Dspring-boot.run.profiles=dev-no-auth
```

### 2. Start Keycloak (For Full JWT Testing)
```bash
# Terminal 2: Start Keycloak
cd podman
podman-compose up -d

# Verify Keycloak is running
curl -s http://localhost:8090/health | jq
```

### 3. Verify Application Status
```bash
# Check application health
curl -s http://localhost:8080/api/public/health
```

Expected response:
```json
{"status":"UP","auth":"not required"}
```

## Quick Testing

### Automated Quick Test
Use the provided script for rapid validation:

```bash
# Test with nexus-user role
./scripts/quick-test.sh

# Test with specific user
./scripts/quick-test.sh nexus-user
./scripts/quick-test.sh nexus-manager
./scripts/quick-test.sh nexus-admin
```

### Manual Quick Test
```bash
# 1. Test public endpoints (should work without authentication)
curl http://localhost:8080/api/public/hello
curl http://localhost:8080/api/public/health

# 2. Test protected endpoints (should return 401 without JWT)
curl -w "HTTP %{http_code}\n" http://localhost:8080/api/hello
curl -w "HTTP %{http_code}\n" http://localhost:8080/api/user/hello
curl -w "HTTP %{http_code}\n" http://localhost:8080/api/admin/hello
```

## Comprehensive Testing

### Using Test Scripts

#### 1. Full JWT Testing Script
```bash
# Comprehensive testing with role validation
./scripts/test-jwt.sh nexus-user
./scripts/test-jwt.sh nexus-manager
./scripts/test-jwt.sh nexus-admin
```

This script will:
- Obtain JWT token from Keycloak
- Decode and display user information
- Test all endpoint access levels
- Show appropriate permissions for each role

#### 2. Script Output Example
```
🔐 Testing JWT for user: nexus-user
✅ Token obtained successfully

ℹ️ User Information:
   Name: Nexus User
   Email: user@nexus.systech.com
   Username: nexus-user
   Roles: nexus-user
   Expires: 2025-09-18 20:45:64

ℹ️ Testing endpoints for user: nexus-user
==================================================
✅ Public   endpoint: 200 - {"message":"Hello, World!"}
✅ User     endpoint: 200 - {"message":"Hello nexus-user!"}
🚫 Manager  endpoint: 403 - Forbidden
🚫 Admin    endpoint: 403 - Forbidden
```

## Manual Testing

### 1. Obtain JWT Token

#### Get Token for Different Users
```bash
# For nexus-user
curl -X POST http://localhost:8090/realms/nexus-dev/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=nexus-web-app" \
  -d "username=nexus-user" \
  -d "password=nexus123"

# For nexus-admin
curl -X POST http://localhost:8090/realms/nexus-dev/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=nexus-web-app" \
  -d "username=nexus-admin" \
  -d "password=nexus123"
```

#### Extract Access Token
```bash
# Store token in variable
TOKEN=$(curl -s -X POST http://localhost:8090/realms/nexus-dev/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password&client_id=nexus-web-app&username=nexus-user&password=nexus123" | \
  python3 -c "import sys, json; print(json.load(sys.stdin)['access_token'])")

echo "Token: $TOKEN"
```

### 2. Test API Endpoints with JWT

#### Test All Endpoint Categories
```bash
# Public endpoints (no authentication required)
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/public/hello
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/public/health

# User-level endpoints (requires nexus-user role or higher)
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/hello
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/user/hello

# Manager-level endpoints (requires nexus-manager role or higher)
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/manager/hello

# Admin-level endpoints (requires nexus-admin role)
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/admin/hello
```

#### GraphQL Testing
```bash
# Test GraphQL with JWT
curl -X POST http://localhost:8080/graphql \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"query":"{ hello }"}'

# Test GraphQL user query
curl -X POST http://localhost:8080/graphql \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"query":"{ users { id username email } }"}'
```

### 3. Test Error Scenarios

#### Invalid Token Testing
```bash
# Test with invalid token
curl -w "HTTP %{http_code}\n" \
  -H "Authorization: Bearer invalid-token" \
  http://localhost:8080/api/user/hello

# Test with expired token
curl -w "HTTP %{http_code}\n" \
  -H "Authorization: Bearer expired-token" \
  http://localhost:8080/api/user/hello

# Test with missing Authorization header
curl -w "HTTP %{http_code}\n" \
  http://localhost:8080/api/user/hello
```

## Troubleshooting

### Common Issues and Solutions

#### 1. Application Won't Start
```bash
# Check if port 8080 is in use
lsof -i :8080

# Kill processes on port 8080
pkill -f "spring-boot:run"

# Check application logs
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### 2. Keycloak Connection Issues
```bash
# Verify Keycloak is running
curl -s http://localhost:8090/health

# Check Keycloak containers
podman ps | grep keycloak

# Restart Keycloak
cd podman && podman-compose restart
```

#### 3. JWT Token Issues
```bash
# Verify token format (should have 3 parts separated by dots)
echo $TOKEN | tr '.' '\n' | wc -l  # Should return 3

# Decode JWT payload for debugging
echo $TOKEN | cut -d'.' -f2 | base64 -d | python3 -m json.tool
```

#### 4. CORS Issues
If testing from a browser or frontend application:
```javascript
// Verify CORS headers are present
fetch('http://localhost:8080/api/public/hello', {
  method: 'GET',
  headers: {
    'Authorization': 'Bearer ' + token
  }
}).then(response => console.log(response.headers));
```

### Debugging Tips

#### Check Application Logs
```bash
# Enable debug logging
export LOGGING_LEVEL_COM_SYSTECH_NEXUS=DEBUG
export LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_SECURITY=DEBUG

# Restart application with debug logging
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Validate JWT Configuration
```bash
# Check JWT issuer configuration
curl -s http://localhost:8090/realms/nexus-dev/.well-known/openid_configuration | jq

# Verify JWK set
curl -s http://localhost:8090/realms/nexus-dev/protocol/openid-connect/certs | jq
```

## Security Validation

### 1. Endpoint Access Matrix

| Endpoint | No Auth | nexus-user | nexus-manager | nexus-admin |
|----------|---------|------------|---------------|-------------|
| `/api/public/*` | ✅ 200 | ✅ 200 | ✅ 200 | ✅ 200 |
| `/api/hello` | ❌ 401 | ✅ 200 | ✅ 200 | ✅ 200 |
| `/api/user/*` | ❌ 401 | ✅ 200 | ✅ 200 | ✅ 200 |
| `/api/manager/*` | ❌ 401 | ❌ 403 | ✅ 200 | ✅ 200 |
| `/api/admin/*` | ❌ 401 | ❌ 403 | ❌ 403 | ✅ 200 |

### 2. Security Checklist

- [ ] Public endpoints accessible without authentication
- [ ] Protected endpoints reject requests without JWT
- [ ] JWT tokens are properly validated against Keycloak
- [ ] Role-based access control works correctly
- [ ] Invalid/expired tokens are rejected
- [ ] CORS is configured for frontend integration
- [ ] Sensitive endpoints require appropriate roles

### 3. Performance Testing

#### Load Testing with JWT
```bash
# Simple load test with authentication
for i in {1..100}; do
  curl -s -H "Authorization: Bearer $TOKEN" \
    http://localhost:8080/api/user/hello &
done
wait
```

## Test Automation

### Running All Tests
```bash
# Run unit tests
mvn test

# Run integration tests with authentication
mvn test -Dspring.profiles.active=dev

# Run all tests in parallel
mvn test -T 4
```

### Continuous Integration

Add to your CI/CD pipeline:
```yaml
# Example GitHub Actions step
- name: Test JWT Authentication
  run: |
    mvn spring-boot:run -Dspring-boot.run.profiles=dev &
    sleep 30
    ./scripts/quick-test.sh
    pkill -f "spring-boot:run"
```

## Conclusion

This guide provides comprehensive testing coverage for the JWT authentication system. The combination of automated scripts and manual testing ensures robust validation of security controls and proper access management.

For additional help, check:
- Application logs: `logs/application.log`
- Keycloak admin console: http://localhost:8090/admin
- GraphiQL interface: http://localhost:8080/graphiql
- API documentation: `/docs/api-testing.md`