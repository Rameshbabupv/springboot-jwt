# Tasks: PostgreSQL Company Master CRUD Operations

**Input**: Design documents from `/specs/002-next-we-are/`
**Prerequisites**: plan.md, research.md, data-model.md, contracts/, quickstart.md

## Execution Flow (main)
```
1. Load plan.md from feature directory
   → Extract: Java 17, Spring Boot 3.2.0, DGS Framework 8.1.1, PostgreSQL
2. Load design documents:
   → data-model.md: Company entity with audit fields
   → contracts/: GraphQL operations (3 queries, 5 mutations)
   → quickstart.md: CRUD test scenarios, error cases
3. Generate tasks by category:
   → Setup: Database schema request, dependencies check
   → Tests: GraphQL contract tests, integration tests
   → Core: Entity, Repository, Service, GraphQL DataFetcher
   → Integration: Schema deployment, application config
   → Polish: Unit tests, error handling, documentation
4. Apply task rules: Different files = [P], TDD approach
5. Keep simple: 15 tasks total (user requested short and simple)
```

## Format: `[ID] [P?] Description`
- **[P]**: Can run in parallel (different files, no dependencies)
- All paths relative to repository root

## Phase 3.1: Setup
- [ ] **T001** Request PostgreSQL schema from DBA (see data-model.md DDL)
- [ ] **T002** Verify existing dependencies support PostgreSQL and JPA auditing
- [ ] **T003** [P] Create company module directory structure in `src/main/java/com/systech/nexus/company/`

## Phase 3.2: Tests First (TDD) ⚠️ MUST COMPLETE BEFORE 3.3
**CRITICAL: These tests MUST be written and MUST FAIL before ANY implementation**
- [ ] **T004** [P] GraphQL contract test for companies query in `src/test/java/com/systech/nexus/company/graphql/CompanyQueryTest.java`
- [ ] **T005** [P] GraphQL contract test for createCompany mutation in `src/test/java/com/systech/nexus/company/graphql/CompanyMutationTest.java`
- [ ] **T006** [P] Integration test for complete CRUD flow in `src/test/java/com/systech/nexus/company/CompanyIntegrationTest.java`
- [ ] **T007** [P] Security test for admin-only access in `src/test/java/com/systech/nexus/company/CompanySecurityTest.java`

## Phase 3.3: Core Implementation (ONLY after tests are failing)
- [ ] **T008** [P] Company JPA entity in `src/main/java/com/systech/nexus/company/domain/Company.java`
- [ ] **T009** [P] CompanyRepository interface in `src/main/java/com/systech/nexus/company/repository/CompanyRepository.java`
- [ ] **T010** CompanyService with business logic in `src/main/java/com/systech/nexus/company/service/CompanyService.java`
- [ ] **T011** CompanyDataFetcher for GraphQL operations in `src/main/java/com/systech/nexus/company/graphql/CompanyDataFetcher.java`
- [ ] **T012** GraphQL schema file in `src/main/resources/schema/company.graphqls`

## Phase 3.4: Integration
- [ ] **T013** Configure JPA auditing in application configuration
- [ ] **T014** Update application.yml for PostgreSQL datasource

## Phase 3.5: Polish
- [ ] **T015** [P] Add unit tests for validation logic in `src/test/java/com/systech/nexus/company/domain/CompanyTest.java`

## Dependencies
- **Setup first**: T001-T003 must complete before tests
- **Tests before implementation**: T004-T007 before T008-T012
- **Entity foundation**: T008 must complete before T009, T010
- **Service dependency**: T009 complete before T010
- **DataFetcher dependency**: T008, T010 complete before T011
- **Schema deployment**: T012 after T011
- **Configuration last**: T013-T014 after implementation
- **Polish last**: T015 after all implementation

## Parallel Example
```bash
# Launch contract tests together (T004-T007):
# All can run in parallel as they create different test files
Task: "GraphQL contract test for companies query"
Task: "GraphQL contract test for createCompany mutation"
Task: "Integration test for complete CRUD flow"
Task: "Security test for admin-only access"

# Launch core models together (T008-T009):
Task: "Company JPA entity in domain/Company.java"
Task: "CompanyRepository interface in repository/CompanyRepository.java"
```

## Task Details

### T001: Request PostgreSQL Schema from DBA
- **File**: Email/ticket to DBA team
- **Content**: DDL from data-model.md (companies table with indexes)
- **Wait**: DBA confirmation before proceeding

### T004: GraphQL Contract Test - Companies Query
- **File**: `src/test/java/com/systech/nexus/company/graphql/CompanyQueryTest.java`
- **Test**: `companies`, `company(id)`, `searchCompanies` GraphQL operations
- **Assert**: Schema compliance, field presence, type correctness
- **Expected**: Tests FAIL (no implementation yet)

### T005: GraphQL Contract Test - Company Mutations
- **File**: `src/test/java/com/systech/nexus/company/graphql/CompanyMutationTest.java`
- **Test**: `createCompany`, `updateCompany`, `disableCompany`, `reactivateCompany`, `bulkImportCompanies`
- **Assert**: Input validation, return types, error handling
- **Expected**: Tests FAIL (no implementation yet)

### T008: Company JPA Entity
- **File**: `src/main/java/com/systech/nexus/company/domain/Company.java`
- **Include**: Validation annotations, audit listeners, proper JavaDoc
- **Implement**: Getters/setters, equals/hashCode, toString

### T010: CompanyService Business Logic
- **File**: `src/main/java/com/systech/nexus/company/service/CompanyService.java`
- **Methods**: create, findAll, findById, update, disable, reactivate, bulkImport
- **Include**: Transaction annotations, validation, error handling

### T011: CompanyDataFetcher GraphQL Layer
- **File**: `src/main/java/com/systech/nexus/company/graphql/CompanyDataFetcher.java`
- **Annotations**: `@DgsComponent`, `@DgsQuery`, `@DgsMutation`, `@PreAuthorize`
- **Security**: Restrict to app-admins and platform-admins roles

## Notes
- Keep implementation simple (user requested "short and simple")
- Use existing patterns from greeting/user modules
- No new frameworks or dependencies required
- Focus on core CRUD functionality only
- DBA handles all database infrastructure

## Validation Checklist
- [x] All GraphQL operations have corresponding tests
- [x] Company entity has model task
- [x] All tests come before implementation
- [x] Parallel tasks use different files
- [x] Each task specifies exact file path
- [x] Security requirements covered in tests
- [x] Total tasks kept to 15 (short and simple)