# JWT Testing Scripts

This directory contains scripts to test JWT authentication with different user roles.

## Prerequisites

1. **Keycloak Running**: Make sure Keycloak is running on `http://localhost:8090`
2. **Spring Boot App Running**: Start the app with JWT profile: `mvn spring-boot:run -Dspring-boot.run.profiles=dev`
3. **Python** (for Python script): `python3` with `requests` library

## Available Scripts

### 1. Shell Script (Recommended)
```bash
# Make executable (first time only)
chmod +x scripts/test-jwt.sh

# Test different users
./scripts/test-jwt.sh nexus-user
./scripts/test-jwt.sh nexus-manager
./scripts/test-jwt.sh nexus-admin
```

### 2. Python Script
```bash
# Install requests if needed
pip3 install requests

# Test different users
python3 scripts/test-jwt.py nexus-user
python3 scripts/test-jwt.py nexus-manager
python3 scripts/test-jwt.py nexus-admin
```

## Test Users

| Username | Password | Role | Expected Access |
|----------|----------|------|----------------|
| `nexus-user` | `nexus123` | nexus-user | User endpoints only |
| `nexus-manager` | `nexus123` | nexus-manager | User + Manager endpoints |
| `nexus-admin` | `nexus123` | nexus-admin | All endpoints |

## API Endpoints Tested

- **Public**: `/api/hello/public` - No authentication required
- **User**: `/api/user/hello` - Requires `nexus-user` role or higher
- **Manager**: `/api/manager/hello` - Requires `nexus-manager` role or higher
- **Admin**: `/api/admin/hello` - Requires `nexus-admin` role

## Expected Results

### nexus-user
```
✅ Public   endpoint: 200 - Success
✅ User     endpoint: 200 - Success
🔒 Manager  endpoint: 401/403 - Access denied
🔒 Admin    endpoint: 401/403 - Access denied
```

### nexus-manager
```
✅ Public   endpoint: 200 - Success
✅ User     endpoint: 200 - Success
✅ Manager  endpoint: 200 - Success
🔒 Admin    endpoint: 401/403 - Access denied
```

### nexus-admin
```
✅ Public   endpoint: 200 - Success
✅ User     endpoint: 200 - Success
✅ Manager  endpoint: 200 - Success
✅ Admin    endpoint: 200 - Success
```

## Manual Testing

The scripts also output the JWT token for manual testing:

```bash
# Copy the token from script output
TOKEN="eyJhbGciOiJSUzI1NiIsInR5cC..."

# Test manually with curl
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/user/hello
```

## Creating Users Programmatically

### Automatic User Creation (Recommended)
When you start Spring Boot with `dev` profile, it automatically creates test users:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The `DevStartupRunner` will create:
- `nexus-user` with role `nexus-user`
- `nexus-manager` with role `nexus-manager`
- `nexus-admin` with role `nexus-admin`

### Manual User Creation via API
```bash
# Create users via REST API (requires admin token)
./scripts/create-users.sh
```

### Create Custom Users
```bash
# Create a custom user via API
curl -X POST http://localhost:8080/api/admin/users/create \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "custom-user",
    "email": "custom@example.com",
    "firstName": "Custom",
    "lastName": "User",
    "password": "password123",
    "roles": ["nexus-user"]
  }'
```

## Troubleshooting

1. **401 Unauthorized**: Check if Keycloak is running and user credentials are correct
2. **Connection Refused**: Make sure Spring Boot app is running on port 8080
3. **403 Forbidden**: User lacks required role for the endpoint (expected behavior)
4. **Token Decode Errors**: JWT format issue, check Keycloak configuration
5. **User Creation Fails**: Check if Keycloak admin credentials are correct in `application-dev.yml`