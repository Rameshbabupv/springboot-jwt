# Implementation Plan: Core Application Foundation with Hello World Endpoints

**Branch**: `001-i-need-to` | **Date**: 2025-09-17 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/001-i-need-to/spec.md`

## Execution Flow (/plan command scope)
```
1. Load feature spec from Input path
   → If not found: ERROR "No feature spec at {path}"
2. Fill Technical Context (scan for NEEDS CLARIFICATION)
   → Detect Project Type from context (web=frontend+backend, mobile=app+api)
   → Set Structure Decision based on project type
3. Fill the Constitution Check section based on the content of the constitution document.
4. Evaluate Constitution Check section below
   → If violations exist: Document in Complexity Tracking
   → If no justification possible: ERROR "Simplify approach first"
   → Update Progress Tracking: Initial Constitution Check
5. Execute Phase 0 → research.md
   → If NEEDS CLARIFICATION remain: ERROR "Resolve unknowns"
6. Execute Phase 1 → contracts, data-model.md, quickstart.md, agent-specific template file (e.g., `CLAUDE.md` for Claude Code, `.github/copilot-instructions.md` for GitHub Copilot, or `GEMINI.md` for Gemini CLI).
7. Re-evaluate Constitution Check section
   → If new violations: Refactor design, return to Phase 1
   → Update Progress Tracking: Post-Design Constitution Check
8. Plan Phase 2 → Describe task generation approach (DO NOT create tasks.md)
9. STOP - Ready for /tasks command
```

**IMPORTANT**: The /plan command STOPS at step 7. Phases 2-4 are executed by other commands:
- Phase 2: /tasks command creates tasks.md
- Phase 3-4: Implementation execution (manual or via tools)

## Summary
Primary requirement: Establish foundational Spring Boot application with minimal REST and GraphQL hello world endpoints to verify core infrastructure. Technical approach: Monolithic modular Spring Boot architecture with H2 in-memory database for development, PostgreSQL for production, and Keycloak integration for authentication.

## Technical Context
**Language/Version**: Java 17+ with Spring Boot 3.x
**Primary Dependencies**: Spring Boot Web, Spring Boot GraphQL, Spring Boot Data JPA, Spring Boot Security, Keycloak Adapter, H2 Database
**Storage**: H2 in-memory database (development), PostgreSQL (production)
**Testing**: Spring Boot Test, JUnit 5, TestContainers
**Target Platform**: JVM-based server application
**Project Type**: single - monolithic modular application
**Performance Goals**: Basic request-response cycle verification, foundation for future scaling
**Constraints**: Must follow constitutional principles, minimal implementation approach
**Scale/Scope**: Foundation for extensible system, starting with 2 endpoints (REST + GraphQL)

## Constitution Check
*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

✅ **Architecture Compliance**: Monolithic modular design required - Spring Boot naturally supports this through packages and modules
✅ **Data Management Compliance**: H2 in-memory for development, PostgreSQL production transition strategy required
✅ **Authentication Compliance**: Keycloak integration required for all endpoints
✅ **Development Approach Compliance**: Starting with minimal hello world endpoints as specified

**Assessment**: All constitutional requirements align with planned technical approach. No violations detected.

## Project Structure

### Documentation (this feature)
```
specs/001-i-need-to/
├── plan.md              # This file (/plan command output)
├── research.md          # Phase 0 output (/plan command)
├── data-model.md        # Phase 1 output (/plan command)
├── quickstart.md        # Phase 1 output (/plan command)
├── contracts/           # Phase 1 output (/plan command)
└── tasks.md             # Phase 2 output (/tasks command - NOT created by /plan)
```

### Source Code (repository root)
```
# Option 1: Single project (DEFAULT)
src/
├── main/java/
│   └── com/systech/nexus/
│       ├── NexusApplication.java
│       ├── config/
│       │   ├── SecurityConfig.java
│       │   ├── GraphQLConfig.java
│       │   └── DatabaseConfig.java
│       ├── controller/
│       │   └── HelloController.java
│       ├── graphql/
│       │   └── HelloGraphQLController.java
│       ├── service/
│       │   └── HelloService.java
│       └── model/
│           └── Greeting.java
└── main/resources/
    ├── application.yml
    ├── application-dev.yml
    └── application-prod.yml

test/
├── integration/
├── contract/
└── unit/
```

**Structure Decision**: Option 1 (Single project) - Monolithic modular Spring Boot application as per constitutional requirements

## Phase 0: Outline & Research
1. **Extract unknowns from Technical Context** above:
   - Spring Boot 3.x best practices for modular monolith
   - Keycloak integration patterns with Spring Security
   - H2 to PostgreSQL migration strategy
   - GraphQL with Spring Boot implementation patterns

2. **Generate and dispatch research agents**:
   ```
   Task: "Research Spring Boot 3.x modular monolith architecture patterns"
   Task: "Research Keycloak integration with Spring Boot Security"
   Task: "Research H2 in-memory to PostgreSQL migration strategies"
   Task: "Research Spring Boot GraphQL implementation best practices"
   ```

3. **Consolidate findings** in `research.md` using format:
   - Decision: [what was chosen]
   - Rationale: [why chosen]
   - Alternatives considered: [what else evaluated]

**Output**: research.md with all technical decisions documented

## Phase 1: Design & Contracts
*Prerequisites: research.md complete*

1. **Extract entities from feature spec** → `data-model.md`:
   - Greeting entity with message field
   - Simple value object for hello world responses

2. **Generate API contracts** from functional requirements:
   - REST endpoint: GET /api/hello
   - GraphQL query: hello
   - Output OpenAPI and GraphQL schemas to `/contracts/`

3. **Generate contract tests** from contracts:
   - REST contract test for /api/hello endpoint
   - GraphQL contract test for hello query
   - Tests must fail (no implementation yet)

4. **Extract test scenarios** from user stories:
   - Integration test for REST endpoint accessibility
   - Integration test for GraphQL query functionality
   - Quickstart validation steps

5. **Update agent file incrementally** (O(1) operation):
   - Run `.specify/scripts/bash/update-agent-context.sh claude` for Claude Code
   - Add Spring Boot, Keycloak, H2/PostgreSQL context
   - Update with current modular monolith patterns
   - Keep under 150 lines for token efficiency

**Output**: data-model.md, /contracts/*, failing tests, quickstart.md, CLAUDE.md

## Phase 2: Task Planning Approach
*This section describes what the /tasks command will do - DO NOT execute during /plan*

**Task Generation Strategy**:
- Load `.specify/templates/tasks-template.md` as base
- Generate tasks from Phase 1 design docs (contracts, data model, quickstart)
- REST contract → REST contract test task [P]
- GraphQL contract → GraphQL contract test task [P]
- Greeting entity → model creation task [P]
- Hello service → service implementation task
- Security configuration → Keycloak integration task
- Database configuration → H2/PostgreSQL setup task

**Ordering Strategy**:
- TDD order: Contract tests before implementation
- Dependency order: Models → Services → Controllers → Security → Configuration
- Mark [P] for parallel execution (independent files)

**Estimated Output**: 15-20 numbered, ordered tasks in tasks.md

**IMPORTANT**: This phase is executed by the /tasks command, NOT by /plan

## Phase 3+: Future Implementation
*These phases are beyond the scope of the /plan command*

**Phase 3**: Task execution (/tasks command creates tasks.md)
**Phase 4**: Implementation (execute tasks.md following constitutional principles)
**Phase 5**: Validation (run tests, execute quickstart.md, performance validation)

## Complexity Tracking
*No constitutional violations detected - this section remains empty*

## Progress Tracking
*This checklist is updated during execution flow*

**Phase Status**:
- [ ] Phase 0: Research complete (/plan command)
- [ ] Phase 1: Design complete (/plan command)
- [ ] Phase 2: Task planning complete (/plan command - describe approach only)
- [ ] Phase 3: Tasks generated (/tasks command)
- [ ] Phase 4: Implementation complete
- [ ] Phase 5: Validation passed

**Gate Status**:
- [x] Initial Constitution Check: PASS
- [ ] Post-Design Constitution Check: PASS
- [ ] All NEEDS CLARIFICATION resolved
- [ ] Complexity deviations documented

---
*Based on Application Constitution - See `/CONSTITUTION.md`*