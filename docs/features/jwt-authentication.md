# JWT Authentication Feature

## Overview

The Nexus application implements enterprise-grade JWT (JSON Web Token) authentication using Spring Security OAuth2 Resource Server integrated with Keycloak as the identity provider.

## Architecture

### Components

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Spring Boot   │    │   Keycloak      │
│   Application   │    │   Resource      │    │   Identity      │
│                 │    │   Server        │    │   Provider      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │   1. Request Token    │                       │
         │──────────────────────────────────────────────▶│
         │                       │                       │
         │   2. JWT Token        │                       │
         │◀──────────────────────────────────────────────│
         │                       │                       │
         │   3. API Request      │                       │
         │   + Bearer Token      │                       │
         │──────────────────────▶│                       │
         │                       │   4. Validate Token   │
         │                       │──────────────────────▶│
         │                       │                       │
         │                       │   5. Token Valid      │
         │                       │◀──────────────────────│
         │                       │                       │
         │   6. API Response     │                       │
         │◀──────────────────────│                       │
```

### Security Configuration

- **JWT Validation**: Automatic token validation against Keycloak's public keys
- **Role-Based Access Control**: Three-tier permission system
- **CORS Support**: Configured for frontend integration
- **Stateless**: No server-side session management

## Role Hierarchy

### Role Definitions

| Role | Level | Permissions | Endpoints Access |
|------|-------|-------------|------------------|
| `nexus-user` | 1 | Basic user operations | `/api/hello`, `/api/user/*` |
| `nexus-manager` | 2 | User management, reports | All user permissions + `/api/manager/*` |
| `nexus-admin` | 3 | Full system administration | All permissions + `/api/admin/*` |

### Permission Matrix

```
Permission Level    │ nexus-user │ nexus-manager │ nexus-admin
────────────────────┼────────────┼───────────────┼─────────────
Public Endpoints    │     ✅     │      ✅       │     ✅
User Operations     │     ✅     │      ✅       │     ✅
Manager Operations  │     ❌     │      ✅       │     ✅
Admin Operations    │     ❌     │      ❌       │     ✅
User Management     │     ❌     │      ✅       │     ✅
System Config       │     ❌     │      ❌       │     ✅
```

## Implementation Details

### Key Classes

#### Security Configuration
```java
// src/main/java/com/systech/nexus/config/SecurityConfig.java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    // JWT decoder configuration
    // CORS configuration
    // Security filter chain
}
```

#### JWT Token Utility
```java
// src/main/java/com/systech/nexus/common/util/JwtTokenUtil.java
@Component
public class JwtTokenUtil {
    // Token extraction and validation
    // User information retrieval
    // Role extraction
}
```

#### Keycloak Integration
```java
// src/main/java/com/systech/nexus/config/KeycloakAdminConfig.java
@Configuration
@Profile("dev")
public class KeycloakAdminConfig {
    // Admin client configuration
    // User management setup
}
```

### Endpoint Security

#### Public Endpoints (No Authentication)
```java
@GetMapping("/api/public/hello")
public ResponseEntity<Map<String, String>> publicHello() {
    // Accessible without authentication
}
```

#### Protected Endpoints (JWT Required)
```java
@GetMapping("/api/user/hello")
@PreAuthorize("hasRole('nexus-user')")
public ResponseEntity<Map<String, String>> userHello() {
    // Requires nexus-user role or higher
}

@GetMapping("/api/admin/hello")
@PreAuthorize("hasRole('nexus-admin')")
public ResponseEntity<Map<String, String>> adminHello() {
    // Requires nexus-admin role
}
```

## Configuration

### Application Properties

#### Development Profile (`application-dev.yml`)
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8090/realms/nexus-dev
          jwk-set-uri: http://localhost:8090/realms/nexus-dev/protocol/openid-connect/certs

nexus:
  security:
    cors:
      allowed-origins:
        - http://localhost:3000
        - http://localhost:3001
        - http://localhost:8080
```

#### Keycloak Configuration
```yaml
keycloak:
  admin:
    server-url: http://localhost:8090
    realm: nexus-dev
    client-id: admin-cli
    username: admin
    password: admin
```

### Test Users

Default test users created automatically in development:

| Username | Password | Roles | Email |
|----------|----------|-------|-------|
| `nexus-user` | `nexus123` | nexus-user | user@nexus.systech.com |
| `nexus-manager` | `nexus123` | nexus-user, nexus-manager | manager@nexus.systech.com |
| `nexus-admin` | `nexus123` | nexus-user, nexus-manager, nexus-admin | admin@nexus.systech.com |

## Usage Examples

### Frontend Integration

#### React/JavaScript Example
```javascript
// Get JWT token
const getToken = async (username, password) => {
  const response = await fetch('http://localhost:8090/realms/nexus-dev/protocol/openid-connect/token', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: new URLSearchParams({
      grant_type: 'password',
      client_id: 'nexus-web-app',
      username,
      password
    })
  });

  const data = await response.json();
  return data.access_token;
};

// Use token for API calls
const callAPI = async (endpoint, token) => {
  const response = await fetch(`http://localhost:8080${endpoint}`, {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });

  return response.json();
};

// Example usage
const token = await getToken('nexus-user', 'nexus123');
const userInfo = await callAPI('/api/user/hello', token);
```

#### curl Examples
```bash
# Get token
TOKEN=$(curl -s -X POST "http://localhost:8090/realms/nexus-dev/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password&client_id=nexus-web-app&username=nexus-user&password=nexus123" | \
  jq -r '.access_token')

# Use token
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/user/hello
```

### GraphQL Integration

```bash
# GraphQL with JWT
curl -X POST http://localhost:8080/graphql \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"query":"{ users { id username email } }"}'
```

## Testing

### Automated Testing Scripts

```bash
# Quick validation
./scripts/quick-test.sh

# Comprehensive testing
./scripts/test-jwt.sh nexus-user
./scripts/test-jwt.sh nexus-manager
./scripts/test-jwt.sh nexus-admin
```

### Unit Tests

```java
@Test
@WithMockUser(roles = "nexus-user")
void shouldAllowUserAccess() throws Exception {
    mockMvc.perform(get("/api/user/hello"))
           .andExpect(status().isOk());
}

@Test
@WithMockUser(roles = "nexus-user")
void shouldDenyAdminAccess() throws Exception {
    mockMvc.perform(get("/api/admin/hello"))
           .andExpect(status().isForbidden());
}
```

## Security Considerations

### Token Security
- **Expiration**: Tokens expire automatically (configurable in Keycloak)
- **Scope**: Tokens include only necessary scopes and roles
- **Validation**: All tokens validated against Keycloak's public keys
- **Revocation**: Tokens can be revoked through Keycloak admin interface

### Best Practices
- ✅ Use HTTPS in production
- ✅ Store tokens securely in frontend (httpOnly cookies or secure storage)
- ✅ Implement token refresh mechanism
- ✅ Log security events for auditing
- ✅ Regular security key rotation
- ✅ Implement rate limiting

### CORS Configuration
- Configured for specific origins only
- Includes necessary headers for JWT authentication
- Preflight request handling for complex requests

## Monitoring and Debugging

### Logging
```yaml
logging:
  level:
    com.systech.nexus: DEBUG
    org.springframework.security: DEBUG
    org.springframework.security.oauth2: DEBUG
```

### Health Checks
```bash
# Application health
curl http://localhost:8080/api/public/health

# Keycloak health
curl http://localhost:8090/health
```

### JWT Token Debugging
```bash
# Decode JWT payload
echo $TOKEN | cut -d'.' -f2 | base64 -d | python3 -m json.tool
```

## Migration and Deployment

### Environment-Specific Configuration

#### Development
- Uses local Keycloak instance
- Debug logging enabled
- Test users auto-created

#### Production
- External Keycloak instance
- Minimal logging
- User management through admin interface
- HTTPS enforcement

### Database Integration
- User information cached in local H2/PostgreSQL
- Automatic user sync from Keycloak
- Audit trail for user operations

## Troubleshooting

### Common Issues

1. **Token Validation Failures**
   - Check Keycloak connectivity
   - Verify issuer URI configuration
   - Ensure JWK set is accessible

2. **CORS Errors**
   - Verify allowed origins configuration
   - Check preflight request handling
   - Ensure proper headers in requests

3. **Role-Based Access Issues**
   - Verify user roles in Keycloak
   - Check @PreAuthorize annotations
   - Validate role mapping configuration

### Support
- Testing Guide: `/docs/jwt-authentication-testing.md`
- API Documentation: `/docs/api-testing.md`
- Configuration Reference: `CLAUDE.md`

## Future Enhancements

- [ ] Token refresh automation
- [ ] Multi-factor authentication
- [ ] Advanced role hierarchies
- [ ] OAuth2 social login integration
- [ ] API rate limiting by role
- [ ] Detailed audit logging
- [ ] Token analytics and monitoring