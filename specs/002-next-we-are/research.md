# Research: PostgreSQL Company Master CRUD

**Date**: 2025-09-27
**Feature**: Company Master CRUD via GraphQL
**Approach**: Keep it short and simple

## Research Questions Resolved

### 1. PostgreSQL Integration with Spring Boot
**Decision**: Use Spring Data JPA with PostgreSQL driver
**Rationale**:
- Already established pattern in project
- Provides JPA entity mapping
- Handles connection pooling and transactions
- Compatible with existing Spring Boot 3.2.0

**Alternatives considered**:
- Direct JDBC (rejected - too low-level)
- MyBatis (rejected - adds complexity)

### 2. GraphQL Schema Design for Company CRUD
**Decision**: Simple schema with basic CRUD operations
**Rationale**:
- Follows existing DGS Framework patterns in project
- Clear separation: Query for reads, Mutation for writes
- Admin-only operations match security requirements

**Alternatives considered**:
- REST endpoints (rejected - spec requires GraphQL)
- Complex nested operations (rejected - keep simple)

### 3. Audit Trail Implementation
**Decision**: Use JPA @CreatedDate/@LastModifiedDate annotations
**Rationale**:
- Spring Boot provides built-in auditing support
- Automatic timestamp management
- User tracking via SecurityContext integration
- Minimal code required

**Alternatives considered**:
- Manual audit fields (rejected - error-prone)
- Separate audit table (rejected - over-engineered)

### 4. Database Schema Strategy
**Decision**: Request schema from DBA, implement JPA entities
**Rationale**:
- DBA handles infrastructure (per role boundaries)
- Backend developer implements entity mapping only
- Clear separation of responsibilities

**Alternatives considered**:
- Auto-DDL generation (rejected - not DBA-managed)
- Manual schema creation (rejected - outside role)

### 5. Testing Strategy
**Decision**: Use existing test patterns from project
**Rationale**:
- JUnit 5 + Spring Boot Test already established
- DGS Test Framework for GraphQL testing
- Repository tests for data layer
- Integration tests for complete flows

**Alternatives considered**:
- New testing frameworks (rejected - constitutional violation)
- TestContainers (deferred - keep simple for now)

## Implementation Approach

### Database Access Pattern
- JPA Entity with validation annotations
- Spring Data JPA repository interface
- Service layer for business logic
- GraphQL DataFetcher for API layer

### Security Integration
- Use existing Spring Security JWT setup
- `@PreAuthorize` annotations for role checking
- app-admins and platform-admins access only

### Error Handling
- Spring Boot default error handling
- Custom validation messages
- GraphQL error responses

## No Further Research Required
All technical decisions made with existing project patterns. Implementation can proceed directly to Phase 1 design artifacts.