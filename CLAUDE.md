# Claude Development Guidelines

This file contains essential guidelines for Claude when working on this project to ensure constitutional compliance and best practices.

## 🚨 Constitutional Requirements (NON-NEGOTIABLE)

### GitFlow Workflow - MANDATORY
- **NEVER** commit directly to `main` branch
- **ALWAYS** create feature branches from `develop`
- **ALWAYS** follow the pattern: `feature/<meaningful-name>`
- **ALWAYS** test thoroughly before merging
- **ALWAYS** delete feature branch after successful merge

### Branch Structure
```
main (production-ready code only)
├── develop (integration branch)
    ├── feature/aop-logging
    ├── feature/jwt-auth
    ├── feature/database-entities
    └── feature/<next-feature>
```

### Required Commands Before ANY Development
```bash
# 1. Check current branch
git branch

# 2. Switch to develop (if not already)
git checkout develop

# 3. Pull latest changes
git pull origin develop

# 4. Create feature branch
git checkout -b feature/<meaningful-name>

# 5. Start development work
```

### Required Commands Before Committing
```bash
# 1. Run tests
mvn test

# 2. Run application to verify it starts
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 3. Check for linting/formatting issues (when available)
# mvn spotless:check

# 4. Stage and commit changes
git add .
git commit -m "feat: meaningful commit message

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>"
```

## 🔧 Technical Standards

### Build System
- **Maven** is the required build system (NOT Gradle for now)
- **DGS Framework** for GraphQL (version 8.1.1)
- **Spring Boot 3.2.0** with Java 17

### Testing Requirements
- **ALWAYS** run `mvn test` before committing
- **ALWAYS** ensure application starts successfully
- **TDD approach** preferred for new features
- **Test coverage** should not decrease

### Code Quality
- **Follow existing patterns** in the codebase
- **Use @Loggable annotation** for new methods requiring logging
- **Maintain consistent naming** conventions
- **Add documentation** for new features in `docs/features/`
- **Follow documentation standards** as defined in "Documentation Guidelines" section

## 📋 Development Checklist

Before starting ANY new feature:
- [ ] Confirm current branch is `develop`
- [ ] Pull latest changes from `develop`
- [ ] Create new feature branch: `feature/<name>`
- [ ] Update CLAUDE.md if new guidelines needed

Before committing ANY changes:
- [ ] Run `mvn test` (all tests pass)
- [ ] Run `mvn spring-boot:run -Dspring-boot.run.profiles=dev` (app starts)
- [ ] Verify no constitutional violations
- [ ] Stage and commit with proper message format

Before merging feature:
- [ ] All tests passing
- [ ] Feature fully implemented and tested
- [ ] Documentation updated
- [ ] Ready to merge to `develop`
- [ ] Delete feature branch after merge

## 🚨 Current Violation Alert

**STATUS**: ⚠️ AOP logging was implemented directly on branch `001-i-need-to` without following GitFlow

**REQUIRED FIX**:
1. Check git status
2. Create proper feature branch: `feature/aop-logging`
3. Move AOP changes to feature branch
4. Follow proper GitFlow process

## 📖 Quick Reference Commands

```bash
# GitFlow workflow
git checkout develop
git pull origin develop
git checkout -b feature/my-feature
# ... do development work ...
mvn test && mvn spring-boot:run -Dspring-boot.run.profiles=dev
git add . && git commit -m "feat: description"
git checkout develop
git merge feature/my-feature
git branch -d feature/my-feature
git push origin develop

# Testing commands
mvn test                                          # Run all tests
mvn spring-boot:run -Dspring-boot.run.profiles=dev  # Start in dev mode

# API testing
curl http://localhost:8080/api/hello
curl http://localhost:8080/api/health
curl -X POST http://localhost:8080/graphql -H "Content-Type: application/json" -d '{"query":"{ hello }"}'
```

## 📁 Project Structure Understanding

```
src/main/java/com/systech/nexus/
├── common/
│   ├── annotation/     # Custom annotations (@Loggable)
│   └── aspect/         # AOP aspects (LoggingAspect)
├── config/             # Configuration classes (AopConfig)
├── greeting/           # Feature modules
│   ├── controller/     # REST controllers
│   ├── domain/         # Domain models
│   ├── graphql/        # GraphQL resolvers
│   └── service/        # Business logic
└── NexusApplication.java
```

## 📚 Documentation Guidelines (MANDATORY)

### Class-Level Documentation Standards

**ALL classes MUST include comprehensive documentation following the 3-Level Strategy:**

#### 1. Major Changes Changelog (In-Class)
```java
/**
 * ClassName description here.
 *
 * MAJOR CHANGES:
 * v1.0 (YYYY-MM-DD) - Initial implementation with core functionality
 * v1.1 (YYYY-MM-DD) - Added validation layer and error handling
 * v1.2 (YYYY-MM-DD) - Refactored architecture for better separation of concerns
 *
 * For complete change history: git log --follow ClassName.java
 *
 * Features:
 * - List key features and capabilities
 * - Explain architectural decisions
 * - Document important constraints or patterns
 *
 * @author Author Name
 * @version 1.2
 * @since 1.0
 */
```

#### 2. What to Include in In-Class Changelog
**INCLUDE** (Major changes only):
- ✅ Initial implementation
- ✅ Major architectural changes
- ✅ Breaking API changes
- ✅ Significant refactoring
- ✅ Design pattern changes
- ✅ Major feature additions

**DON'T INCLUDE** (Use git history):
- ❌ Bug fixes
- ❌ Minor enhancements
- ❌ Code style changes
- ❌ Dependency updates
- ❌ Performance optimizations
- ❌ Documentation updates

#### 3. Layer-Specific Documentation Requirements

**🏛️ ENTITY CLASSES (@Entity):**
```java
/**
 * Entity description and database mapping info.
 *
 * MAJOR CHANGES:
 * v1.0 (2025-01-17) - Initial implementation with basic fields
 * v1.1 (2025-01-20) - Added validation constraints and audit fields
 *
 * Database Details:
 * - Table: table_name
 * - Primary Key: field_name (strategy)
 * - Unique Constraints: field1, field2
 * - Indexes: index descriptions
 *
 * Business Rules:
 * - Document important business logic
 * - Validation rules and constraints
 * - Relationships and dependencies
 */
```

**🔧 SERVICE CLASSES (@Service):**
```java
/**
 * Service description and business logic overview.
 *
 * MAJOR CHANGES:
 * v1.0 (2025-01-17) - Initial CRUD operations
 * v1.1 (2025-01-20) - Added transaction management and validation
 * v1.2 (2025-01-25) - Refactored to use composition pattern
 *
 * Responsibilities:
 * - List primary business responsibilities
 * - Transaction boundaries
 * - Integration points
 * - Error handling strategy
 *
 * Dependencies:
 * - Repository dependencies
 * - External service integrations
 * - Configuration requirements
 */
```

**🌐 CONTROLLER/DATAFETCHER CLASSES:**
```java
/**
 * API layer description and endpoint overview.
 *
 * MAJOR CHANGES:
 * v1.0 (2025-01-17) - Initial REST/GraphQL endpoints
 * v1.1 (2025-01-20) - Added comprehensive error handling
 * v1.2 (2025-01-25) - Added input validation and security
 *
 * API Contract:
 * - List main endpoints/operations
 * - Input/output formats
 * - Error response formats
 * - Authentication requirements
 *
 * Features:
 * - Request/response transformation
 * - Validation and sanitization
 * - Error handling strategy
 */
```

**💾 REPOSITORY INTERFACES:**
```java
/**
 * Data access layer description and query strategy.
 *
 * MAJOR CHANGES:
 * v1.0 (2025-01-17) - Initial CRUD queries
 * v1.1 (2025-01-20) - Added custom search queries
 * v1.2 (2025-01-25) - Optimized query performance
 *
 * Query Strategy:
 * - Custom query explanations
 * - Performance considerations
 * - Index usage
 * - Case sensitivity rules
 */
```

### Method-Level Documentation

**REQUIRED for all public methods:**
```java
/**
 * Method description explaining purpose and behavior.
 *
 * @param paramName description of parameter
 * @return description of return value
 * @throws ExceptionType when and why this exception occurs
 * @since version when method was added
 */
```

### GraphQL Schema Documentation

**ALL GraphQL schemas MUST include:**
```graphql
# Schema file header with purpose and version
#
# Author: Name
# Version: 1.x
# Last Updated: YYYY-MM-DD

"""
Type description explaining the entity and its purpose.
Include business context and usage notes.
"""
type EntityName {
    "Field description with constraints and format"
    fieldName: String!
}
```

### Documentation Maintenance Rules

1. **ALWAYS update version number** when making changes
2. **ALWAYS add major changes** to changelog
3. **ALWAYS reference git history** for detailed changes
4. **NEVER let documentation become stale**
5. **ALWAYS document breaking changes** with migration notes

### Enforcement Checklist

Before committing ANY class:
- [ ] Class has proper header documentation
- [ ] Major changes are documented in changelog
- [ ] Version number is updated if changed
- [ ] All public methods have JavaDoc
- [ ] Author and @since tags present
- [ ] Feature descriptions are current

## 🔐 JWT Authentication Implementation (COMPLETED)

### **Status**: ✅ IMPLEMENTED - `feature/jwt-authentication` branch

**JWT Authentication is now fully integrated with the following components:**

#### Core Security Configuration
- **SecurityConfig.java**: Complete Spring Security configuration with JWT validation
  - OAuth2 Resource Server setup
  - Role-based access control (RBAC)
  - CORS configuration for React frontend
  - H2 console access for development
  - Method-level security annotations support

#### JWT Token Management
- **JwtTokenUtil.java**: Comprehensive JWT token utility class
  - Extract user information (ID, username, email, roles)
  - Role validation methods (`hasRole()`, `isAdmin()`, `isManagerOrAdmin()`)
  - Custom claims extraction
  - Null-safe operations

#### Security Integration
- **HelloController**: Updated with role-based endpoint security
  - Public endpoints: `/api/public/**`
  - User endpoints: `/api/user/**` (requires any authenticated user)
  - Manager endpoints: `/api/manager/**` (requires manager or admin)
  - Admin endpoints: `/api/admin/**` (requires admin only)

- **UserDataFetcher**: GraphQL operations secured with JWT
  - Queries: Require authentication (any valid user)
  - Create/Update: Require manager or admin role
  - Delete: Require admin role only

- **TestController**: JWT validation testing endpoints
  - `/api/test/public` - No authentication
  - `/api/test/user` - User level access
  - `/api/test/manager` - Manager level access
  - `/api/test/admin` - Admin level access
  - `/api/test/token-info` - JWT token debugging
  - `/api/test/role-check` - Role validation testing

#### Configuration Files
- **application.yml**: Base JWT configuration with Keycloak integration
- **application-dev.yml**: Development profile with CORS settings

#### Dependencies Added
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-oauth2-jose</artifactId>
</dependency>
```

### Role-Based Access Control (RBAC)

**Keycloak Roles**: nexus-admin, nexus-manager, nexus-user, nexus-viewer

**Access Levels**:
- **Public**: No authentication required
- **User**: Any authenticated user (`@PreAuthorize("hasAnyRole('nexus-user', 'nexus-manager', 'nexus-admin')")`)
- **Manager**: Manager or Admin (`@PreAuthorize("hasAnyRole('nexus-manager', 'nexus-admin')")`)
- **Admin**: Admin only (`@PreAuthorize("hasRole('nexus-admin')")`)

### Testing JWT Integration

**Prerequisites**: Keycloak must be running (use podman configuration)

```bash
# 1. Start Keycloak (from podman directory)
./start-keycloak.sh

# 2. Get JWT Token
curl -X POST http://localhost:8090/realms/nexus-dev/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=nexus-web-app" \
  -d "username=nexus-user" \
  -d "password=nexus123"

# 3. Test Public Endpoint (no token required)
curl http://localhost:8080/api/test/public

# 4. Test Protected Endpoint (with token)
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     http://localhost:8080/api/test/user

# 5. Test GraphQL (with token)
curl -X POST http://localhost:8080/graphql \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"query": "{ users { id username email } }"}'
```

### Integration Notes

- **Backward Compatibility**: Legacy endpoints maintained but now require authentication
- **AOP Logging**: `@Loggable` annotation compatibility preserved
- **Error Handling**: Proper 401/403 responses for authentication/authorization failures
- **Development Mode**: H2 console and public endpoints available for development

### Next Integration Steps

1. **Start Keycloak**: Use podman configuration to start authentication server
2. **Test Endpoints**: Verify JWT validation with test endpoints
3. **Frontend Integration**: Connect React application with Keycloak
4. **Production Setup**: Configure production Keycloak instance

## 🎯 Next Development Priorities

1. **Test JWT Integration**: Start Keycloak and verify authentication flow
2. **Frontend Integration**: Connect React application with Keycloak authentication
3. **Database Entities**: Create `feature/database-entities` branch
4. **GraphQL Mutations**: Enhanced GraphQL operations with security

---

**Remember**: This file should be consulted before EVERY development task to ensure constitutional compliance!