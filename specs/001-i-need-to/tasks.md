# Tasks: Core Application Foundation with Hello World Endpoints

**Input**: Design documents from `/specs/001-i-need-to/`
**Prerequisites**: plan.md (required), research.md, spec.md

## Execution Flow (main)
```
1. Load plan.md from feature directory
   → If not found: ERROR "No implementation plan found"
   → Extract: tech stack, libraries, structure
2. Load optional design documents:
   → data-model.md: Extract entities → model tasks
   → contracts/: Each file → contract test task
   → research.md: Extract decisions → setup tasks
3. Generate tasks by category:
   → Setup: project init, dependencies, linting
   → Tests: contract tests, integration tests
   → Core: models, services, CLI commands
   → Integration: DB, middleware, logging
   → Polish: unit tests, performance, docs
4. Apply task rules:
   → Different files = mark [P] for parallel
   → Same file = sequential (no [P])
   → Tests before implementation (TDD)
5. Number tasks sequentially (T001, T002...)
6. Generate dependency graph
7. Create parallel execution examples
8. Validate task completeness:
   → All contracts have tests?
   → All entities have models?
   → All endpoints implemented?
9. Return: SUCCESS (tasks ready for execution)
```

## Format: `[ID] [P?] Description`
- **[P]**: Can run in parallel (different files, no dependencies)
- Include exact file paths in descriptions

## Path Conventions
- **Single project**: `src/`, `test/` at repository root
- Paths assume Spring Boot single modular monolith structure per plan.md

## Phase 3.1: Project Setup & Dependencies
- [ ] T001 Initialize Spring Boot 3.x project with Gradle in repository root
- [ ] T002 [P] Configure build.gradle with Spring Boot Web, Spring Boot Security, Spring Boot Data JPA, DGS Framework, H2, PostgreSQL driver dependencies
- [ ] T003 [P] Create application.yml configuration for H2 development database in src/main/resources/application.yml
- [ ] T004 [P] Create application-dev.yml for H2 PostgreSQL compatibility mode in src/main/resources/application-dev.yml
- [ ] T005 [P] Create application-prod.yml for PostgreSQL production config in src/main/resources/application-prod.yml
- [ ] T006 [P] Configure Gradle wrapper and project structure for modular monolith

## Phase 3.2: Tests First (TDD) ⚠️ MUST COMPLETE BEFORE 3.3
**CRITICAL: These tests MUST be written and MUST FAIL before ANY implementation**
- [ ] T007 [P] Contract test REST GET /api/hello endpoint in src/test/java/com/systech/nexus/contract/HelloRestContractTest.java
- [ ] T008 [P] Contract test GraphQL hello query in src/test/java/com/systech/nexus/contract/HelloGraphQLContractTest.java
- [ ] T009 [P] Integration test REST endpoint accessibility in src/test/java/com/systech/nexus/integration/HelloRestIntegrationTest.java
- [ ] T010 [P] Integration test GraphQL query functionality in src/test/java/com/systech/nexus/integration/HelloGraphQLIntegrationTest.java
- [ ] T011 [P] Application startup test verifying both endpoints initialization in src/test/java/com/systech/nexus/ApplicationStartupTest.java

## Phase 3.3: Data Model & Domain Layer (ONLY after tests are failing)
- [ ] T012 [P] Create Greeting entity/model in src/main/java/com/systech/nexus/greeting/domain/Greeting.java
- [ ] T013 [P] Create GreetingService interface in src/main/java/com/systech/nexus/greeting/api/GreetingService.java
- [ ] T014 [P] Implement HelloService with hello world logic in src/main/java/com/systech/nexus/greeting/service/HelloService.java

## Phase 3.4: API Implementation
- [ ] T015 Create REST HelloController with GET /api/hello endpoint in src/main/java/com/systech/nexus/greeting/controller/HelloController.java
- [ ] T016 Create GraphQL HelloGraphQLController with hello query resolver in src/main/java/com/systech/nexus/greeting/graphql/HelloGraphQLController.java
- [ ] T017 [P] Create GraphQL schema definition in src/main/resources/schema/hello.graphqls
- [ ] T018 Create main Spring Boot Application class in src/main/java/com/systech/nexus/NexusApplication.java

## Phase 3.5: Configuration & Security
- [ ] T019 [P] Create DatabaseConfig for H2/PostgreSQL setup in src/main/java/com/systech/nexus/config/DatabaseConfig.java
- [ ] T020 [P] Create GraphQLConfig for DGS Framework setup in src/main/java/com/systech/nexus/config/GraphQLConfig.java
- [ ] T021 Create SecurityConfig for Keycloak OAuth2 integration in src/main/java/com/systech/nexus/config/SecurityConfig.java
- [ ] T022 [P] Configure CORS settings in SecurityConfig for web client access
- [ ] T023 [P] Add Flyway migration scripts in src/main/resources/db/migration/

## Phase 3.6: Integration & Validation
- [ ] T024 Configure application startup logging for endpoint initialization
- [ ] T025 Add error handling for malformed requests in controllers
- [ ] T026 Verify all tests pass with implemented functionality
- [ ] T027 [P] Create manual testing documentation in docs/manual-testing.md
- [ ] T028 [P] Update README.md with quickstart instructions

## Phase 3.7: Polish & Documentation
- [ ] T029 [P] Add unit tests for GreetingService in src/test/java/com/systech/nexus/greeting/service/HelloServiceTest.java
- [ ] T030 [P] Add unit tests for Greeting model in src/test/java/com/systech/nexus/greeting/domain/GreetingTest.java
- [ ] T031 [P] Performance validation tests ensuring basic response times
- [ ] T032 [P] Code cleanup and removal of any duplication
- [ ] T033 [P] Final integration test run and validation

## Dependencies
- Setup (T001-T006) before tests (T007-T011)
- Tests (T007-T011) before implementation (T012-T025)
- Data model (T012-T014) before API implementation (T015-T018)
- T015 depends on T012, T014 (REST controller needs service and model)
- T016 depends on T012, T014, T017 (GraphQL controller needs service, model, schema)
- T021 depends on T019, T020 (Security config needs other configs)
- Integration (T024-T026) before polish (T027-T033)

## Parallel Execution Examples

### Phase 3.1 Setup (Can run T002-T006 in parallel after T001):
```
Task: "Configure build.gradle with Spring Boot dependencies"
Task: "Create application.yml for H2 development database"
Task: "Create application-dev.yml for PostgreSQL compatibility"
Task: "Create application-prod.yml for PostgreSQL production"
Task: "Configure Gradle wrapper and project structure"
```

### Phase 3.2 Tests (All can run in parallel):
```
Task: "Contract test REST GET /api/hello endpoint"
Task: "Contract test GraphQL hello query"
Task: "Integration test REST endpoint accessibility"
Task: "Integration test GraphQL query functionality"
Task: "Application startup test for endpoints initialization"
```

### Phase 3.3 Domain Layer (Can run T012-T014 in parallel):
```
Task: "Create Greeting entity/model"
Task: "Create GreetingService interface"
Task: "Implement HelloService with hello world logic"
```

## Constitutional Compliance
- ✅ Monolithic modular architecture (Spring Modulith package structure)
- ✅ H2 development → PostgreSQL production (T003-T005, T019, T023)
- ✅ Keycloak authentication integration (T021)
- ✅ Minimal hello world foundation (T007-T018)
- ✅ TDD approach with tests first (Phase 3.2 before 3.3)

## Notes
- [P] tasks = different files, no dependencies
- Verify all tests fail before implementing (Phase 3.2 gate)
- Commit after each significant task completion
- Follow Spring Modulith package conventions per research.md
- Ensure both REST and GraphQL endpoints return consistent messages per FR-003

## Task Generation Rules Applied
1. **From Specification Requirements**: FR-001 → T007,T015 | FR-002 → T008,T016,T017 | FR-007 → T001-T006
2. **From Plan Structure**: Modular monolith → package-based tasks | Spring Boot → configuration tasks
3. **From Research Decisions**: DGS Framework → T008,T016,T017,T020 | OAuth2 → T021 | Flyway → T023
4. **TDD Ordering**: All contract/integration tests (T007-T011) before implementation (T012+)

## Validation Checklist
- [x] All functional requirements have corresponding tests
- [x] All entities have model creation tasks
- [x] All tests come before implementation (Phase 3.2 → 3.3)
- [x] Parallel tasks are truly independent (different files)
- [x] Each task specifies exact file path
- [x] No [P] task modifies same file as another [P] task
- [x] Constitutional requirements addressed in task breakdown