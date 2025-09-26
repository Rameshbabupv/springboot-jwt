# User CRUD GraphQL Feature

## Overview

The User CRUD GraphQL feature provides a complete user management system through GraphQL API endpoints. It implements full CRUD (Create, Read, Update, Delete) operations with JWT authentication, role-based access control, and comprehensive error handling.

## Architecture

### Components Overview

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   GraphQL       │    │   UserDataFetcher│   │   UserService   │
│   Client        │    │   (Controller)   │   │   (Business)    │
│                 │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │   GraphQL Query       │                       │
         │   + JWT Token         │                       │
         │──────────────────────▶│                       │
         │                       │   Business Logic      │
         │                       │──────────────────────▶│
         │                       │                       │
         │                       │                       │
         │   GraphQL Response    │                       │
         │◀──────────────────────│                       │
```

### Security Model

The feature implements a three-tier security model:

| Operation | Required Role | Description |
|-----------|---------------|-------------|
| **Queries** | `nexus-user+` | Any authenticated user can read user data |
| **Create/Update** | `nexus-manager+` | Manager or admin can modify users |
| **Delete** | `nexus-admin` | Only admin can delete users |

## GraphQL Schema

### User Type Definition

```graphql
"""
User entity representing a registered user in the system.
Contains personal information, contact details, and audit timestamps.
"""
type User {
    "Unique identifier for the user"
    id: ID!
    "Unique username for login (case-sensitive)"
    username: String!
    "User's email address (unique, case-insensitive)"
    email: String!
    "User's first name (optional)"
    firstName: String
    "User's last name (optional)"
    lastName: String
    "ISO 8601 timestamp when the user was created"
    createdAt: String!
    "ISO 8601 timestamp when the user was last updated"
    updatedAt: String!
}
```

### Input Types

#### CreateUserInput
```graphql
"""
Input type for creating a new user.
All required fields must be provided and will be validated.
"""
input CreateUserInput {
    "Username (3-50 characters, must be unique)"
    username: String!
    "Email address (must be valid format and unique)"
    email: String!
    "First name (optional, max 50 characters)"
    firstName: String
    "Last name (optional, max 50 characters)"
    lastName: String
}
```

#### UpdateUserInput
```graphql
"""
Input type for updating an existing user.
All fields are optional - only provided fields will be updated (partial update).
"""
input UpdateUserInput {
    "New username (must be unique if provided)"
    username: String
    "New email address (must be unique if provided)"
    email: String
    "New first name"
    firstName: String
    "New last name"
    lastName: String
}
```

## Available Operations

### Queries (Read Operations)

#### Get All Users
```graphql
query GetAllUsers {
  users {
    id
    username
    email
    firstName
    lastName
    createdAt
    updatedAt
  }
}
```

#### Get User by ID
```graphql
query GetUser($id: ID!) {
  user(id: $id) {
    id
    username
    email
    firstName
    lastName
    createdAt
    updatedAt
  }
}
```

#### Get User by Username
```graphql
query GetUserByUsername($username: String!) {
  userByUsername(username: $username) {
    id
    username
    email
    firstName
    lastName
    createdAt
    updatedAt
  }
}
```

### Mutations (Write Operations)

#### Create User
```graphql
mutation CreateUser($input: CreateUserInput!) {
  createUser(input: $input) {
    id
    username
    email
    firstName
    lastName
    createdAt
    updatedAt
  }
}
```

**Example Variables:**
```json
{
  "input": {
    "username": "john_doe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe"
  }
}
```

#### Update User
```graphql
mutation UpdateUser($id: ID!, $input: UpdateUserInput!) {
  updateUser(id: $id, input: $input) {
    id
    username
    email
    firstName
    lastName
    updatedAt
  }
}
```

**Example Variables:**
```json
{
  "id": "1",
  "input": {
    "email": "john.doe@example.com",
    "firstName": "Johnny"
  }
}
```

#### Delete User
```graphql
mutation DeleteUser($id: ID!) {
  deleteUser(id: $id)
}
```

## Implementation Details

### Key Classes

#### UserDataFetcher
**Location**: `src/main/java/com/systech/nexus/user/graphql/UserDataFetcher.java`

```java
@DgsComponent
public class UserDataFetcher {
    // Query operations with @DgsQuery
    // Mutation operations with @DgsMutation
    // Custom data fetchers for timestamp formatting
    // Comprehensive error handling with GraphQL errors
    // JWT role-based security with @PreAuthorize
}
```

**Key Features:**
- **DGS Framework Integration**: Uses Netflix DGS annotations
- **Security Integration**: JWT role-based access control
- **Error Handling**: Returns `DataFetcherResult` with proper GraphQL errors
- **Validation**: Input validation with detailed error messages
- **Logging**: AOP logging integration with `@Loggable`

#### User Entity
**Location**: `src/main/java/com/systech/nexus/user/domain/User.java`

```java
@Entity
@Table(name = "users")
public class User {
    // JPA entity with validation annotations
    // Unique constraints on username and email
    // Automatic timestamp management
    // Business methods for common operations
}
```

**Key Features:**
- **JPA Entity**: Full database mapping with constraints
- **Validation**: Bean validation with custom error messages
- **Timestamps**: Automatic creation and update timestamps
- **Business Logic**: Methods like `getFullName()` and `updateFrom()`

#### GraphQL Schema
**Location**: `src/main/resources/schema/user.graphqls`

- Schema-first approach with detailed type definitions
- Comprehensive documentation for all types and fields
- Input validation rules documented in schema
- Follows GraphQL best practices

### Database Design

#### User Table Structure
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_username (username),
    INDEX idx_email (email)
);
```

## Usage Examples

### Frontend Integration

#### React/JavaScript Example
```javascript
import { ApolloClient, InMemoryCache, createHttpLink, gql } from '@apollo/client';
import { setContext } from '@apollo/client/link/context';

// Apollo Client setup with JWT
const httpLink = createHttpLink({
  uri: 'http://localhost:8080/graphql',
});

const authLink = setContext((_, { headers }) => {
  const token = localStorage.getItem('jwt_token');
  return {
    headers: {
      ...headers,
      authorization: token ? `Bearer ${token}` : "",
    }
  }
});

const client = new ApolloClient({
  link: authLink.concat(httpLink),
  cache: new InMemoryCache()
});

// Query users
const GET_USERS = gql`
  query GetUsers {
    users {
      id
      username
      email
      firstName
      lastName
      createdAt
    }
  }
`;

const { loading, error, data } = useQuery(GET_USERS);

// Create user
const CREATE_USER = gql`
  mutation CreateUser($input: CreateUserInput!) {
    createUser(input: $input) {
      id
      username
      email
      firstName
      lastName
    }
  }
`;

const [createUser] = useMutation(CREATE_USER);

const handleCreateUser = async (userData) => {
  try {
    const { data } = await createUser({
      variables: { input: userData }
    });
    console.log('User created:', data.createUser);
  } catch (error) {
    console.error('Error creating user:', error.message);
  }
};
```

#### curl Examples

```bash
# Get JWT token first
TOKEN=$(curl -s -X POST "http://localhost:8090/realms/nexus-dev/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password&client_id=nexus-web-app&username=nexus-manager&password=nexus123" | \
  jq -r '.access_token')

# Query all users
curl -X POST http://localhost:8080/graphql \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "{ users { id username email firstName lastName createdAt } }"
  }' | jq

# Create new user
curl -X POST http://localhost:8080/graphql \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation($input: CreateUserInput!) { createUser(input: $input) { id username email firstName lastName } }",
    "variables": {
      "input": {
        "username": "jane_doe",
        "email": "jane@example.com",
        "firstName": "Jane",
        "lastName": "Doe"
      }
    }
  }' | jq

# Update user
curl -X POST http://localhost:8080/graphql \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation($id: ID!, $input: UpdateUserInput!) { updateUser(id: $id, input: $input) { id username email firstName lastName updatedAt } }",
    "variables": {
      "id": "1",
      "input": {
        "firstName": "Janet"
      }
    }
  }' | jq

# Delete user (admin only)
curl -X POST http://localhost:8080/graphql \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation($id: ID!) { deleteUser(id: $id) }",
    "variables": { "id": "1" }
  }' | jq
```

### GraphiQL Testing

Access the interactive GraphQL IDE at `http://localhost:8080/graphiql` for development testing.

**Sample Queries to Test:**

1. **Test Authentication** (should fail without JWT):
```graphql
{ users { id username } }
```

2. **Test with Valid Token** (add to headers):
```json
{
  "Authorization": "Bearer YOUR_JWT_TOKEN"
}
```

3. **Test Role-Based Access** (try create/delete with different roles)

## Security Features

### JWT Authentication
- **Token Validation**: Every request validates JWT against Keycloak
- **Role Extraction**: Extracts roles from `realm_access.roles` in JWT
- **Error Handling**: Proper 401/403 responses for auth failures

### Role-Based Access Control
```java
@DgsQuery
@PreAuthorize("hasAnyRole('nexus-user', 'nexus-manager', 'nexus-admin')")
public DataFetcherResult<List<User>> users() {
    // Any authenticated user can read
}

@DgsMutation
@PreAuthorize("hasAnyRole('nexus-manager', 'nexus-admin')")
public DataFetcherResult<User> createUser(@InputArgument Map<String, Object> input) {
    // Manager or admin required for create/update
}

@DgsMutation
@PreAuthorize("hasRole('nexus-admin')")
public DataFetcherResult<Boolean> deleteUser(@InputArgument String id) {
    // Admin only for delete operations
}
```

### Input Validation
- **Required Fields**: Username and email are mandatory
- **Format Validation**: Email format validation
- **Length Constraints**: Username (3-50), names (max 50), email (max 100)
- **Uniqueness**: Username and email must be unique
- **SQL Injection Protection**: JPA prepared statements

### Error Handling
```java
// Example of comprehensive error handling
try {
    User newUser = userService.createUser(username, email, firstName, lastName);
    return DataFetcherResult.<User>newResult()
        .data(newUser)
        .build();
} catch (Exception e) {
    GraphQLError error = GraphqlErrorBuilder.newError()
        .message("Failed to create user: " + e.getMessage())
        .build();
    return DataFetcherResult.<User>newResult()
        .error(error)
        .build();
}
```

## Testing

### Automated Testing Scripts

```bash
# Test user CRUD operations with different roles
./scripts/test-jwt.sh nexus-user     # Can only read
./scripts/test-jwt.sh nexus-manager  # Can read, create, update
./scripts/test-jwt.sh nexus-admin    # Can read, create, update, delete
```

### Unit Tests

```java
@Test
@WithMockUser(roles = "nexus-user")
void shouldAllowUserToQueryUsers() throws Exception {
    // Test read access for user role
}

@Test
@WithMockUser(roles = "nexus-user")
void shouldDenyUserCreateAccess() throws Exception {
    // Test that users cannot create
}

@Test
@WithMockUser(roles = "nexus-admin")
void shouldAllowAdminDeleteAccess() throws Exception {
    // Test admin delete permissions
}
```

### GraphQL Testing Checklist

- [ ] **Authentication Tests**
  - [ ] Unauthenticated requests return 401
  - [ ] Invalid JWT tokens return 401
  - [ ] Expired JWT tokens return 401

- [ ] **Authorization Tests**
  - [ ] User role can query users
  - [ ] User role cannot create/update/delete
  - [ ] Manager role can create/update but not delete
  - [ ] Admin role can perform all operations

- [ ] **Validation Tests**
  - [ ] Missing username returns validation error
  - [ ] Missing email returns validation error
  - [ ] Invalid email format returns validation error
  - [ ] Duplicate username/email returns constraint error

- [ ] **Business Logic Tests**
  - [ ] Users created with proper timestamps
  - [ ] Partial updates work correctly
  - [ ] Full name concatenation works
  - [ ] User lookup by ID and username

## Performance Considerations

### Query Optimization
- **Database Indexes**: Indexes on username and email for fast lookups
- **N+1 Problem**: Single database queries for user operations
- **Pagination**: Consider implementing for large user lists

### Caching Strategy
- **Entity Caching**: JPA second-level cache for frequent lookups
- **Query Caching**: Cache expensive queries like user searches
- **JWT Validation**: Cache public keys for token validation

### Monitoring
- **GraphQL Metrics**: Monitor query performance and error rates
- **Database Monitoring**: Track slow queries and connection usage
- **Security Audit**: Log all user modifications for compliance

## Error Scenarios & Troubleshooting

### Common Issues

#### 1. Authentication Failures
**Symptoms**: 401 Unauthorized responses
**Solutions**:
- Verify JWT token is valid and not expired
- Check Keycloak connectivity and configuration
- Ensure Authorization header format: `Bearer <token>`

#### 2. Permission Denied
**Symptoms**: 403 Forbidden responses
**Solutions**:
- Verify user has required role in Keycloak
- Check @PreAuthorize annotation permissions
- Confirm role mapping in JWT claims

#### 3. Validation Errors
**Symptoms**: GraphQL validation errors
**Solutions**:
- Check required fields (username, email)
- Validate email format
- Ensure username/email uniqueness

#### 4. Database Constraints
**Symptoms**: Unique constraint violations
**Solutions**:
- Check existing users before creation
- Handle duplicate key exceptions gracefully
- Provide meaningful error messages to clients

### Debug Mode

Enable debug logging for troubleshooting:

```yaml
logging:
  level:
    com.systech.nexus.user: DEBUG
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG
```

## Future Enhancements

### Planned Features
- [ ] **Pagination Support**: Implement cursor-based pagination for user lists
- [ ] **Search Functionality**: Add full-text search on user fields
- [ ] **Bulk Operations**: Batch create/update/delete operations
- [ ] **User Profiles**: Extended user information and preferences
- [ ] **Audit Trail**: Complete audit log for all user changes
- [ ] **Email Verification**: Email confirmation workflow
- [ ] **Password Management**: Password reset and change operations

### Integration Opportunities
- [ ] **Keycloak User Sync**: Two-way sync with Keycloak user store
- [ ] **File Uploads**: User avatar and document support
- [ ] **Notification System**: User activity notifications
- [ ] **Role Management**: Dynamic role assignment through GraphQL
- [ ] **Organization Support**: Multi-tenant user management
- [ ] **Analytics**: User activity and engagement metrics

## Migration Guide

### From REST to GraphQL
If migrating from REST endpoints:

1. **Update Client Code**: Replace REST calls with GraphQL queries
2. **Authentication**: Same JWT token authentication
3. **Error Handling**: Adapt to GraphQL error format
4. **Caching**: Update client-side caching for GraphQL

### Database Migration
The user table schema is compatible with existing REST implementations:
- No breaking changes to table structure
- Same validation rules and constraints
- Backward compatible with existing data

### Configuration Updates
Update application properties for GraphQL:
```yaml
# GraphQL configuration
spring:
  graphql:
    graphiql:
      enabled: true
      path: /graphiql
```

---

## Support

- **Documentation**: This file and `/docs/api-testing.md`
- **Testing Scripts**: `/scripts/test-jwt.sh` for automated testing
- **Configuration**: `CLAUDE.md` for project guidelines
- **GraphQL IDE**: `http://localhost:8080/graphiql` for interactive testing

For additional support or feature requests, refer to the project's main documentation and development guidelines.