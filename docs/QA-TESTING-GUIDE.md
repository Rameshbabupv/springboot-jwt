# QA Testing Guide - Nexus Application

## 📋 Overview

This comprehensive guide provides QA teams with complete testing procedures for the Nexus Spring Boot application, covering unit testing, integration testing, end-to-end testing, and all application features.

## 🎯 Application Features to Test

- ✅ **Hello World REST API** - Basic HTTP endpoints
- ✅ **JWT Authentication** - Role-based security with Keycloak
- ✅ **User CRUD GraphQL** - Complete user management via GraphQL
- ✅ **AOP Logging** - Method-level logging functionality
- ✅ **Database Integration** - H2 in-memory database
- ✅ **CORS Configuration** - Cross-origin request handling

## 📦 QA Environment Setup

### Prerequisites Installation

```bash
# Required Software (install in order)
# 1. Java 17+
java -version
# Expected: openjdk version "17.x.x"

# 2. Maven 3.8+
mvn -version
# Expected: Apache Maven 3.8.x

# 3. curl (for API testing)
curl --version

# 4. jq (for JSON parsing)
jq --version

# 5. Python 3 (for JWT scripts)
python3 --version

# 6. Podman/Docker (for Keycloak)
podman --version
```

### Download & Setup Project

```bash
# 1. Clone/Download the repository
git clone <repository-url>
cd springboot-jwt

# 2. Verify project structure
ls -la
# Expected: pom.xml, src/, docs/, scripts/, podman/

# 3. Clean build to verify setup
mvn clean compile
# Expected: BUILD SUCCESS

# 4. Run initial tests
mvn test
# Expected: Tests run: 6, Failures: 0, Errors: 0
```

## 🧪 Complete Testing Workflow

### Phase 1: Unit Testing

#### 1.1 Run Unit Tests
```bash
# Run all unit tests
mvn test

# Expected Output:
# Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
# BUILD SUCCESS
```

#### 1.2 Generate Test Reports
```bash
# Generate detailed test reports
mvn surefire-report:report

# View reports at: target/site/surefire-report.html
open target/site/surefire-report.html
```

#### 1.3 Unit Test Validation Checklist
- [ ] HelloControllerTest: 3 tests pass
  - [ ] `shouldReturnPublicHelloMessage()` - Public API access
  - [ ] `shouldReturnPublicHealthCheck()` - Health endpoint
  - [ ] `shouldAllowAllEndpointsInTestEnvironment()` - Test security config
- [ ] HelloServiceTest: 3 tests pass
  - [ ] `shouldReturnHelloWorldMessage()` - Basic greeting
  - [ ] `shouldReturnCustomGreeting()` - Custom name greeting
  - [ ] `shouldHandleNullName()` - Input validation

### Phase 2: Integration Testing

#### 2.1 Application Startup Testing

```bash
# Test 1: Application starts without auth
mvn spring-boot:run -Dspring-boot.run.profiles=dev-no-auth

# Validation:
# - Watch for "Started NexusApplication" in logs
# - No error messages in startup
# - Application runs on port 8080

# Test 2: Application starts with JWT auth (requires Keycloak)
# Start Keycloak first
cd podman && ./start-keycloak.sh

# Start application
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Validation:
# - Application connects to Keycloak
# - JWT configuration loads successfully
# - No security configuration errors
```

#### 2.2 Database Integration Testing

```bash
# While application is running, test H2 database
# Access H2 console: http://localhost:8080/h2-console

# Database Connection Settings:
# - JDBC URL: jdbc:h2:mem:testdb
# - User Name: sa
# - Password: password

# Validation Queries:
SELECT * FROM users;  -- Should show user table structure
-- Note: Users table may be empty initially
```

### Phase 3: API Testing (REST Endpoints)

#### 3.1 Public Endpoints (No Authentication)

```bash
# Test public hello endpoint
curl -w "\nStatus: %{http_code}\n" http://localhost:8080/api/public/hello

# Expected:
# {"message":"Hello, World!"}
# Status: 200

# Test public health endpoint
curl -w "\nStatus: %{http_code}\n" http://localhost:8080/api/public/health

# Expected:
# {"status":"UP","auth":"not required"}
# Status: 200
```

#### 3.2 JWT Authentication Testing

```bash
# Use provided test script
./scripts/quick-test.sh

# Expected Output:
# 🔐 Testing JWT for user: nexus-user
# ✅ Token obtained
# 🧪 Testing endpoints:
#   Public:  200
#   User:    200
#   Manager: 403
#   Admin:   403

# Test different user roles
./scripts/quick-test.sh nexus-manager
./scripts/quick-test.sh nexus-admin
```

#### 3.3 Role-Based Access Testing

```bash
# Get tokens for different users
USER_TOKEN=$(./scripts/quick-test.sh nexus-user | tail -1)
MANAGER_TOKEN=$(./scripts/quick-test.sh nexus-manager | tail -1)
ADMIN_TOKEN=$(./scripts/quick-test.sh nexus-admin | tail -1)

# Test user level access
curl -H "Authorization: Bearer $USER_TOKEN" http://localhost:8080/api/user/hello
# Expected: 200 OK

curl -H "Authorization: Bearer $USER_TOKEN" http://localhost:8080/api/admin/hello
# Expected: 403 Forbidden

# Test manager level access
curl -H "Authorization: Bearer $MANAGER_TOKEN" http://localhost:8080/api/manager/hello
# Expected: 200 OK

# Test admin level access
curl -H "Authorization: Bearer $ADMIN_TOKEN" http://localhost:8080/api/admin/hello
# Expected: 200 OK
```

### Phase 4: GraphQL Testing

#### 4.1 GraphQL Endpoint Testing

```bash
# Test GraphQL without authentication (should fail)
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"{ users { id username } }"}'

# Expected: 401 Unauthorized

# Test GraphQL with authentication
curl -X POST http://localhost:8080/graphql \
  -H "Authorization: Bearer $USER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"query":"{ users { id username email } }"}'

# Expected: 200 OK with user data
```

#### 4.2 GraphQL User CRUD Testing

```bash
# Test user creation (requires manager token)
curl -X POST http://localhost:8080/graphql \
  -H "Authorization: Bearer $MANAGER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation($input: CreateUserInput!) { createUser(input: $input) { id username email } }",
    "variables": {
      "input": {
        "username": "testuser1",
        "email": "test1@example.com",
        "firstName": "Test",
        "lastName": "User"
      }
    }
  }'

# Expected: 200 OK with created user data

# Test user update
curl -X POST http://localhost:8080/graphql \
  -H "Authorization: Bearer $MANAGER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation($id: ID!, $input: UpdateUserInput!) { updateUser(id: $id, input: $input) { id firstName } }",
    "variables": {
      "id": "1",
      "input": {
        "firstName": "Updated"
      }
    }
  }'

# Test user deletion (requires admin token)
curl -X POST http://localhost:8080/graphql \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation($id: ID!) { deleteUser(id: $id) }",
    "variables": { "id": "1" }
  }'
```

#### 4.3 GraphQL Interactive Testing

```bash
# Access GraphiQL IDE
open http://localhost:8080/graphiql

# Test queries interactively:
# 1. Add Authorization header: Bearer <token>
# 2. Run sample queries:

# Sample Query 1: Get all users
{
  users {
    id
    username
    email
    firstName
    lastName
    createdAt
  }
}

# Sample Query 2: Get user by ID
{
  user(id: "1") {
    username
    email
    fullName
  }
}

# Sample Mutation: Create user
mutation {
  createUser(input: {
    username: "graphql_user"
    email: "graphql@test.com"
    firstName: "GraphQL"
    lastName: "Test"
  }) {
    id
    username
    email
  }
}
```

### Phase 5: End-to-End Testing Scenarios

#### 5.1 User Registration & Management Flow

```bash
# Scenario: Complete user lifecycle
# Prerequisites: Application and Keycloak running, Manager token available

echo "🔄 Testing complete user lifecycle..."

# Step 1: Create user
CREATE_RESPONSE=$(curl -s -X POST http://localhost:8080/graphql \
  -H "Authorization: Bearer $MANAGER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation($input: CreateUserInput!) { createUser(input: $input) { id username email } }",
    "variables": {
      "input": {
        "username": "e2e_user",
        "email": "e2e@test.com",
        "firstName": "E2E",
        "lastName": "Test"
      }
    }
  }')

echo "Create Response: $CREATE_RESPONSE"

# Step 2: Query created user
USER_ID=$(echo $CREATE_RESPONSE | jq -r '.data.createUser.id')
curl -s -X POST http://localhost:8080/graphql \
  -H "Authorization: Bearer $USER_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"query\":\"{ user(id: \\\"$USER_ID\\\") { username email firstName lastName } }\"}" | jq

# Step 3: Update user
curl -s -X POST http://localhost:8080/graphql \
  -H "Authorization: Bearer $MANAGER_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"query\": \"mutation(\\\$id: ID!, \\\$input: UpdateUserInput!) { updateUser(id: \\\$id, input: \\\$input) { firstName lastName } }\",
    \"variables\": {
      \"id\": \"$USER_ID\",
      \"input\": {
        \"firstName\": \"Updated E2E\",
        \"lastName\": \"Updated Test\"
      }
    }
  }" | jq

# Step 4: Delete user (admin required)
curl -s -X POST http://localhost:8080/graphql \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"query\":\"mutation(\\\$id: ID!) { deleteUser(id: \\\$id) }\", \"variables\": {\"id\": \"$USER_ID\"}}" | jq

echo "✅ User lifecycle test completed"
```

#### 5.2 Authentication & Authorization Flow

```bash
echo "🔐 Testing authentication and authorization flow..."

# Test 1: Access without token (should fail)
echo "Test 1: No token access"
curl -s -o /dev/null -w "Status: %{http_code}\n" http://localhost:8080/api/user/hello

# Test 2: Invalid token (should fail)
echo "Test 2: Invalid token"
curl -s -o /dev/null -w "Status: %{http_code}\n" \
  -H "Authorization: Bearer invalid.token.here" \
  http://localhost:8080/api/user/hello

# Test 3: Valid token, insufficient permissions
echo "Test 3: User accessing admin endpoint"
curl -s -o /dev/null -w "Status: %{http_code}\n" \
  -H "Authorization: Bearer $USER_TOKEN" \
  http://localhost:8080/api/admin/hello

# Test 4: Valid token, sufficient permissions
echo "Test 4: Admin accessing admin endpoint"
curl -s -o /dev/null -w "Status: %{http_code}\n" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  http://localhost:8080/api/admin/hello

echo "✅ Authentication flow test completed"
```

#### 5.3 Error Handling Testing

```bash
echo "⚠️ Testing error handling..."

# Test 1: Invalid GraphQL query syntax
curl -s -X POST http://localhost:8080/graphql \
  -H "Authorization: Bearer $USER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"query":"{ invalid syntax }"}' | jq

# Test 2: Non-existent user query
curl -s -X POST http://localhost:8080/graphql \
  -H "Authorization: Bearer $USER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"query":"{ user(id: \"999999\") { username } }"}' | jq

# Test 3: Invalid user creation data
curl -s -X POST http://localhost:8080/graphql \
  -H "Authorization: Bearer $MANAGER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation($input: CreateUserInput!) { createUser(input: $input) { id } }",
    "variables": {
      "input": {
        "username": "",
        "email": "invalid-email"
      }
    }
  }' | jq

echo "✅ Error handling test completed"
```

## 📊 Test Results Documentation

### Test Execution Report Template

```markdown
# Test Execution Report - [Date]

## Environment Details
- **Application Version**: [Git commit/tag]
- **Java Version**: [Version]
- **Test Environment**: [dev/qa/staging]
- **Keycloak Version**: [Version if applicable]

## Test Results Summary
- **Unit Tests**: ✅ 6/6 passed
- **Integration Tests**: ✅ All passed
- **API Tests**: ✅ All endpoints working
- **GraphQL Tests**: ✅ All operations working
- **E2E Tests**: ✅ All scenarios passed

## Detailed Results

### Unit Tests
| Test Class | Tests | Passed | Failed | Duration |
|------------|-------|--------|--------|----------|
| HelloControllerTest | 3 | 3 | 0 | 1.2s |
| HelloServiceTest | 3 | 3 | 0 | 0.03s |

### Integration Tests
| Component | Status | Notes |
|-----------|--------|--------|
| Application Startup | ✅ | Started in 2.5s |
| Database Connection | ✅ | H2 console accessible |
| Keycloak Integration | ✅ | JWT validation working |

### API Tests
| Endpoint | Method | Expected | Actual | Status |
|----------|--------|----------|---------|--------|
| /api/public/hello | GET | 200 | 200 | ✅ |
| /api/user/hello | GET | 200 (with JWT) | 200 | ✅ |
| /api/admin/hello | GET | 403 (user token) | 403 | ✅ |

### Issues Found
- [ ] Issue #1: [Description] - Priority: [High/Medium/Low]
- [ ] Issue #2: [Description] - Priority: [High/Medium/Low]

## Recommendations
- ✅ Application is ready for deployment
- ⚠️ Minor issues need attention
- ❌ Blocking issues must be fixed
```

## 🚨 Common Issues & Troubleshooting

### Issue 1: Application Won't Start
**Symptoms**: Application fails during startup
**Solutions**:
```bash
# Check Java version
java -version  # Must be 17+

# Check for port conflicts
lsof -i :8080  # Kill processes using port 8080

# Check Maven build
mvn clean compile

# Check application logs
tail -f logs/application.log
```

### Issue 2: Tests Failing
**Symptoms**: `mvn test` shows failures
**Solutions**:
```bash
# Clean and rerun tests
mvn clean test

# Run specific test class
mvn test -Dtest=HelloControllerTest

# Check test configuration
cat src/test/java/com/systech/nexus/config/TestSecurityConfig.java
```

### Issue 3: JWT Authentication Not Working
**Symptoms**: 401/403 errors with valid tokens
**Solutions**:
```bash
# Check Keycloak is running
curl http://localhost:8090/health

# Verify token format
echo $TOKEN | cut -d'.' -f2 | base64 -d | python3 -m json.tool

# Check application JWT configuration
grep -A 10 "jwt:" src/main/resources/application*.yml
```

### Issue 4: GraphQL Queries Failing
**Symptoms**: GraphQL returns errors
**Solutions**:
```bash
# Test GraphQL endpoint directly
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"{ __schema { types { name } } }"}'

# Check GraphQL schema
curl http://localhost:8080/graphql/schema

# Use GraphiQL for debugging
open http://localhost:8080/graphiql
```

## 📈 Performance Testing Guidelines

### Basic Performance Tests

```bash
# Test concurrent users (requires 'ab' tool)
# Install: brew install apache-bench (macOS) or apt-get install apache2-utils (Linux)

# Test public endpoint performance
ab -n 1000 -c 10 http://localhost:8080/api/public/hello

# Test authenticated endpoint performance
ab -n 100 -c 5 -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/user/hello

# Expected Results:
# - Public endpoints: >500 req/sec
# - Authenticated endpoints: >200 req/sec
# - No failed requests
```

### Memory and Resource Monitoring

```bash
# Monitor application memory usage
# While tests are running:

# Check Java process memory
ps aux | grep java

# Monitor system resources
top -p $(pgrep java)

# Check for memory leaks (run tests multiple times)
# Memory usage should stabilize after warmup
```

## 🎯 QA Checklist for New Features

When testing new features, ensure:

### Code Quality
- [ ] Unit tests cover new functionality
- [ ] Integration tests validate external dependencies
- [ ] Code follows project patterns and conventions
- [ ] Proper error handling implemented

### Security
- [ ] Authentication required for protected endpoints
- [ ] Authorization levels properly enforced
- [ ] Input validation prevents injection attacks
- [ ] Sensitive data not logged or exposed

### Performance
- [ ] Endpoints respond within acceptable time limits
- [ ] Database queries are optimized
- [ ] No memory leaks detected
- [ ] Concurrent access handled properly

### Documentation
- [ ] API endpoints documented
- [ ] Feature documentation created/updated
- [ ] Testing procedures documented
- [ ] README updated if needed

## 🔧 Advanced Testing Scenarios

### Keycloak Integration Testing

```bash
# Test Keycloak realm configuration
curl http://localhost:8090/realms/nexus-dev/.well-known/openid_connect_configuration

# Test user roles in Keycloak
curl -s -X POST "http://localhost:8090/realms/nexus-dev/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password&client_id=nexus-web-app&username=nexus-admin&password=nexus123" | \
  jq -r '.access_token' | cut -d'.' -f2 | base64 -d | python3 -m json.tool

# Verify roles are correctly included in JWT
```

### Database State Testing

```bash
# Test database persistence (if using PostgreSQL in production)
# 1. Create test data
# 2. Restart application
# 3. Verify data persists

# Test database constraints
# Try creating duplicate users, invalid data, etc.

# Test database cleanup
# Verify test data doesn't pollute subsequent tests
```

---

## 📞 QA Support

- **Documentation**: `/docs/` directory for feature-specific guides
- **Scripts**: `/scripts/` directory for automated testing scripts
- **Configuration**: `CLAUDE.md` for project guidelines and standards
- **Issues**: Create GitHub issues for bugs or improvement requests

## 🎯 Success Criteria

A successful QA test run should achieve:
- ✅ All unit tests pass (6/6)
- ✅ Application starts cleanly in both profiles
- ✅ All API endpoints return expected responses
- ✅ JWT authentication works for all user roles
- ✅ GraphQL operations work correctly
- ✅ End-to-end scenarios complete successfully
- ✅ No security vulnerabilities detected
- ✅ Performance within acceptable limits

**QA Sign-off indicates the application is ready for deployment.**