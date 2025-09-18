# Research: Technical Decisions for Spring Boot 3.x Hello World Foundation

## Spring Boot 3.x Modular Monolith Architecture

**Decision**: Use Spring Modulith Framework
**Rationale**: Official Spring approach for modular monoliths with GA status since August 2023. Provides clear module boundaries, architectural verification, and event-driven communication between modules.
**Alternatives considered**: Traditional layered architecture (lacks module boundaries), package-by-feature (insufficient boundary enforcement), manual modular structure (lacks verification)

**Package Structure**:
```
com.systech.nexus/
├── Application.java
├── greeting/              # Hello world module
│   ├── api/              # @NamedInterface exposed APIs
│   ├── service/          # Internal services
│   └── domain/           # Internal domain logic
└── config/               # Shared configuration
    ├── security/
    ├── database/
    └── graphql/
```

## Keycloak Integration with Spring Boot Security

**Decision**: Use Spring Security OAuth2 Resource Server with JWT
**Rationale**: Keycloak adapters are deprecated in Spring Boot 3.x. Native OAuth2 support is more maintainable, portable, and provides stateless JWT validation for better scalability.
**Alternatives considered**: Keycloak Spring Adapters (deprecated), custom authentication (violates constitution), session-based authentication (scalability concerns)

**Configuration Approach**:
- Stateless session management
- JWT authentication with role extraction
- CORS enabled for web clients
- CSRF disabled for API endpoints

## H2 to PostgreSQL Migration Strategy

**Decision**: Use Flyway for Database Migrations with PostgreSQL Compatibility Mode
**Rationale**: Provides versioned, reproducible migrations with excellent Spring Boot 3.x integration. PostgreSQL compatibility mode in H2 ensures schema consistency between environments.
**Alternatives considered**: JPA auto-DDL (lacks control), Liquibase (more complex configuration), manual SQL scripts (lacks automation)

**Migration Strategy**:
1. Development: H2 with PostgreSQL compatibility mode
2. Production: Native PostgreSQL with Flyway migrations
3. Testing: Testcontainers for integration tests

## Spring Boot GraphQL Implementation

**Decision**: Use DGS Framework with Spring GraphQL Integration
**Rationale**: DGS 10.0.0+ provides deep integration with Spring GraphQL, combining Netflix's battle-tested features with Spring ecosystem. Schema-first approach aligns with modular development.
**Alternatives considered**: Pure Spring GraphQL (fewer features), GraphQL Java directly (more boilerplate), third-party solutions (less ecosystem integration)

**Implementation Approach**:
- Schema-first development with SDL format
- Code generation via DGS Gradle plugin
- Data Fetchers using DGS annotations
- DGS testing framework for validation

## Technology Stack Summary

**Core Dependencies**:
- Spring Boot 3.2+
- Spring Modulith 1.3+
- Spring Security OAuth2 Resource Server
- DGS Framework 10.0+
- Flyway Core
- H2 Database (development)
- PostgreSQL Driver (production)
- Testcontainers (testing)

**Development Tools**:
- Java 17+
- Gradle with DGS plugin
- Docker for Keycloak and PostgreSQL
- JUnit 5 for testing

This research provides the foundation for implementing a constitutional-compliant Spring Boot application that can grow from hello world endpoints into a full modular monolith.