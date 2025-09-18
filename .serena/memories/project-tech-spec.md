# Project Tech Spec: Spring Boot JWT with GraphQL Application

## Project Overview
**Repository**: springboot-jwt  
**Branch**: 001-i-need-to  
**Purpose**: Core application foundation with minimal hello world endpoints  
**Architecture**: Monolithic modular Spring Boot application  

## Constitutional Requirements (Non-Negotiable)

### 1. Architecture
- **MUST** be architected as a monolithic modular application
- **MUST** support continuous addition of new functional modules
- Modules **MUST** be loosely coupled and independently deployable within the monolith

### 2. Data Management
- Initial development **MUST** use H2 in-memory database
- Production **MUST** transition to PostgreSQL
- **MUST** have clearly defined database migration strategy
- All data access **MUST** use repository patterns

### 3. Authentication & Authorization
- All auth **MUST** be handled through Keycloak integration
- No custom authentication mechanisms permitted
- All endpoints **MUST** be secured through Keycloak policies
- Role-based access control **MUST** be via Keycloak

### 4. Development Approach
- **MUST** start with minimal, functional endpoints
- Basic "hello world" endpoints **MUST** exist for both REST and GraphQL
- Stable foundation **MUST** be established before module expansion
- Incremental development and testing **MUST** be prioritized

## Technical Stack

### Core Framework
- **Java**: 17+
- **Spring Boot**: 3.2+
- **Spring Modulith**: 1.3+ (for modular monolith architecture)
- **Build Tool**: Gradle with DGS plugin

### Security & Authentication
- **Spring Security**: OAuth2 Resource Server with JWT
- **Keycloak Integration**: Native OAuth2 (adapters deprecated in Spring Boot 3.x)
- **Session Management**: Stateless JWT validation
- **CORS**: Enabled for web clients
- **CSRF**: Disabled for API endpoints

### Database
- **Development**: H2 in-memory with PostgreSQL compatibility mode
- **Production**: PostgreSQL
- **Migrations**: Flyway for versioned database changes
- **Testing**: Testcontainers for integration tests

### GraphQL Implementation
- **DGS Framework**: 10.0+ with Spring GraphQL integration
- **Approach**: Schema-first development with SDL format
- **Code Generation**: DGS Gradle plugin
- **Data Fetchers**: DGS annotations
- **Testing**: DGS testing framework

### Testing Strategy
- **Framework**: JUnit 5
- **Approach**: Test-Driven Development (TDD)
- **Types**: Contract tests, integration tests, unit tests
- **Order**: Tests MUST be written first and MUST fail before implementation

## Project Structure

### Documentation
```
specs/001-i-need-to/
├── spec.md           # Feature specification
├── plan.md           # Implementation plan
├── research.md       # Technical decisions
└── tasks.md          # Execution tasks
```

### Source Code (Spring Modulith Pattern)
```
src/main/java/com/systech/nexus/
├── NexusApplication.java           # Main application
├── greeting/                       # Hello world module
│   ├── api/                       # @NamedInterface exposed APIs
│   │   └── GreetingService.java
│   ├── service/                   # Internal services
│   │   └── HelloService.java
│   ├── domain/                    # Internal domain logic
│   │   └── Greeting.java
│   ├── controller/                # REST controllers
│   │   └── HelloController.java
│   └── graphql/                   # GraphQL controllers
│       └── HelloGraphQLController.java
└── config/                        # Shared configuration
    ├── SecurityConfig.java        # Keycloak OAuth2
    ├── GraphQLConfig.java         # DGS Framework
    └── DatabaseConfig.java        # H2/PostgreSQL
```

### Resources
```
src/main/resources/
├── application.yml                # Base config
├── application-dev.yml           # H2 development
├── application-prod.yml          # PostgreSQL production
├── schema/                       # GraphQL schemas
│   └── hello.graphqls
└── db/migration/                 # Flyway scripts
```

## Functional Requirements

### Core Endpoints
- **FR-001**: REST API endpoint returning hello world greeting (`GET /api/hello`)
- **FR-002**: GraphQL endpoint returning hello world greeting (hello query)
- **FR-003**: Consistent, predefined greeting messages from both endpoints
- **FR-004**: Appropriate HTTP status codes for successful requests
- **FR-005**: System accessible and operational for external requests
- **FR-006**: Basic request-response cycles for both REST and GraphQL
- **FR-007**: Verifiable foundation for future module additions

### Key Entities
- **Greeting Message**: Simple text response containing predefined hello world message

## Implementation Phases

### Phase 1: Project Setup & Dependencies
- Initialize Spring Boot 3.x project with Gradle
- Configure dependencies (Web, Security, Data JPA, DGS Framework, H2, PostgreSQL)
- Create application configuration files for different environments
- Configure Gradle wrapper and project structure

### Phase 2: Tests First (TDD - CRITICAL)
- Contract tests for REST and GraphQL endpoints
- Integration tests for endpoint accessibility
- Application startup tests
- **All tests MUST fail before ANY implementation**

### Phase 3: Data Model & Domain Layer
- Create Greeting entity/model
- Create GreetingService interface
- Implement HelloService with hello world logic

### Phase 4: API Implementation
- REST HelloController with GET /api/hello
- GraphQL HelloGraphQLController with hello query resolver
- GraphQL schema definition
- Main Spring Boot Application class

### Phase 5: Configuration & Security
- DatabaseConfig for H2/PostgreSQL setup
- GraphQLConfig for DGS Framework
- SecurityConfig for Keycloak OAuth2 integration
- CORS settings for web client access
- Flyway migration scripts

### Phase 6: Integration & Validation
- Application startup logging
- Error handling for malformed requests
- Verify all tests pass
- Manual testing documentation
- README with quickstart instructions

## Development Constraints
- No implementation details in business requirements
- TDD approach mandatory (tests before code)
- Constitutional compliance at every phase
- Modular package structure following Spring Modulith patterns
- Stateless authentication only
- Both REST and GraphQL endpoints must return consistent messages
- Each task must specify exact file paths
- Parallel tasks must be truly independent (different files)

## Quality Gates
- Initial Constitution Check: PASS
- Post-Design Constitution Check: Required
- All NEEDS CLARIFICATION resolved: Required
- All tests failing before implementation: Required
- All tests passing after implementation: Required