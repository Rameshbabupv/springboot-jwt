# Architecture Decision Records (ADR) - Spring Boot Application

## ADR-SB-001: Keycloak Authentication System

**Date**: 2025-09-18
**Status**: ✅ Implemented
**Decision Maker**: Development Team

### Context
Need centralized authentication system for Nexus application (React frontend + Spring Boot backend + GraphQL API).

### Decision
- **Chosen**: Keycloak as identity provider
- **Alternative Considered**: Custom JWT implementation
- **Reason**: Industry standard, OAuth2/OIDC compliance, user management UI

---

## ADR-SB-002: Single Client vs Multiple Clients

**Date**: 2025-09-18
**Status**: ✅ Implemented
**Decision Maker**: Development Team

### Context
Initial setup created separate clients for frontend (`nexus-web-app`) and backend (`nexus-api`). Question arose: do we need two clients for one application?

### Decision
- **Chosen**: Single client approach (`nexus-web-app` only)
- **Alternative Rejected**: Separate frontend/backend clients
- **Reason**:
  - Simpler architecture
  - React + Spring Boot = one logical application
  - Frontend gets token, backend validates (no client secret needed)
  - Easier maintenance and testing

### Implementation
- ❌ Deleted: `nexus-api` client
- ✅ Kept: `nexus-web-app` client (public, no client secret)
- ✅ Spring Boot: Uses JWT validation with public keys

---

## ADR-SB-003: Authentication Flow for GraphQL Testing

**Date**: 2025-09-18
**Status**: ✅ Approved
**Decision Maker**: Development Team

### Context
Need way for developers to test GraphQL endpoints during development.

### Decision
- **Testing Flow**:
  1. Get JWT token via REST API call to Keycloak
  2. Use JWT token in Authorization header for GraphQL requests
- **No client secrets**: Spring Boot validates tokens using Keycloak's public keys

### Benefits
- Simple testing workflow
- No secrets to manage for API validation
- Secure: Only Keycloak can create valid tokens

---

## ADR-SB-004: Database Migration from H2 to PostgreSQL

**Date**: 2025-09-27
**Status**: ✅ Approved
**Decision Maker**: Development Team

### Context
Current application uses H2 in-memory database for development. Need production-ready database solution for NEXUS HRMS system with multi-schema architecture for different business domains.

### Decision
- **Chosen**: PostgreSQL migration
- **Target Configuration**:
  - Database: `nexus_hrms`
  - Host: localhost:5432
  - User: rameshbabu
  - Multi-schema approach: `nx_core`, `nx_hr`, `nx_audit`, etc.
- **Alternative Considered**: Continue with H2
- **Reason**:
  - Production readiness requirement
  - Multi-schema support for HRMS modules
  - Better performance and reliability
  - Team expertise with PostgreSQL

### Implementation Plan
- Add PostgreSQL dependency to pom.xml
- Create production database configuration profile
- Maintain H2 for development/testing
- Implement schema-based entity mapping

---

## ADR-SB-005: GraphQL Framework - DGS vs Spring GraphQL

**Date**: 2025-09-27
**Status**: ❌ Denied (DGS Framework Change)
**Decision Maker**: Development Team

### Context
Backend developer notes included Spring GraphQL examples (`@Controller`, `@QueryMapping`) while current implementation uses DGS Framework (`@DgsComponent`, `@DgsQuery`). Question arose about potential framework migration.

### Decision
- **Status**: Migration from DGS to Spring GraphQL is **DENIED**
- **Current Framework**: DGS Framework 8.1.1 - **NO CHANGES PERIOD**
- **Reason for Denial**:
  - Current DGS implementation is stable and working
  - No compelling business reason for framework change
  - Risk of introducing instability
  - Framework changes require strong justification
  - Backend notes were reference examples, not migration requirements

### Clarification
- Backend developer notes showing Spring GraphQL were **examples/alternatives**
- **NOT** a mandate to change frameworks
- DGS Framework remains the **constitutional choice**
- Any future framework changes require separate ADR with strong business justification

---

## ADR-SB-006: Keycloak Realm Migration (nexus-dev → systech)

**Date**: 2025-09-27
**Status**: ✅ Implemented
**Decision Maker**: Development Team

### Context
Application was using `nexus-dev` realm with `nexus-web-app` client. Need to align with actual deployment realm and client configuration.

### Decision
- **New Configuration**:
  - Realm: `systech` (changed from `nexus-dev`)
  - Client: `systech-hrms-client` (changed from `nexus-web-app`)
  - User: `babu.systech` (changed from `nexus-user`)
- **Reason**: Alignment with actual Keycloak deployment configuration

### Implementation
- ✅ Updated application.yml and application-dev.yml
- ✅ Updated all test scripts
- ✅ Updated Java code documentation
- ✅ Verified SecurityConfig compatibility

---

## Application Architecture

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   React     │    │ Spring Boot │    │  Keycloak   │
│  Frontend   │    │   Backend   │    │             │
│             │    │  + GraphQL  │    │             │
│ 1. Login ───┼────┼─────────────┼───→│ systech     │
│ 2. Get JWT  │◄───┼─────────────┼───◄│ realm       │
│ 3. API Call │    │             │    │             │
│   + JWT ────┼───→│ 4. Validate │    │             │
│             │    │    JWT ─────┼───→│ Public Keys │
│ 5. Response │◄───│             │    │             │
└─────────────┘    └─────────────┘    └─────────────┘
```

### Current Technology Stack
- **Framework**: Spring Boot 3.2.0 + Java 17
- **GraphQL**: DGS Framework 8.1.1 (constitutional choice)
- **Build**: Maven
- **Database**: H2 (dev) → PostgreSQL (production, approved)
- **Authentication**: Keycloak JWT (systech realm)
- **Security**: Spring Security OAuth2 Resource Server

---

## Decision Status Legend
- ✅ **Implemented**: Decision made and implemented
- ⚠️ **Approved**: Decision made, implementation pending
- ❌ **Denied**: Decision considered but denied
- 🔄 **Under Review**: Currently being evaluated

---

## Notes
- Keep ADRs focused on Spring Boot application decisions
- Document WHY decisions were made, not just WHAT
- Update status as implementation progresses
- Review periodically for relevance
- Infrastructure/deployment decisions belong in separate ADR documents