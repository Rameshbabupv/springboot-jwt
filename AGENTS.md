# Repository Guidelines

## Project Structure & Module Organization
Source lives under `src/main/java/com/systech/nexus`, organized into `common`, `config`, `greeting`, and `user` modules with controllers in `.controller`, services in `.service`, and domain models in `.domain`. GraphQL schemas sit in `src/main/resources/schema`, shared configuration in `src/main/resources`, and project docs in `docs/`. Tests mirror the main tree under `src/test/java`. Build outputs belong in `target/`, while runtime logs stay in `logs/` and remain untracked.

## Build, Test, and Development Commands
- `mvn clean verify` — full compile, unit/integration tests, and Maven validations before any PR.
- `mvn test` — quick feedback loop for JUnit 5 and Mockito slices.
- `mvn package` — assemble the runnable JAR in `target/`.
- `mvn spring-boot:run -Dspring-boot.run.profiles=dev` — start the API against the in-memory H2 profile.
- `curl http://localhost:8080/api/hello` and `curl -X POST http://localhost:8080/graphql -d '{"query":"{ hello }"}'` — REST and GraphQL smoke checks.

## Coding Style & Naming Conventions
Target Java 17 with four-space indentation. Keep packages lowercase, classes UpperCamelCase, and methods/fields lowerCamelCase. Use profile-specific `application.yml` files; never embed secrets or tokens in code. GraphQL fetchers belong under `.graphql`.

## Testing Guidelines
Adopt TDD only once the system is stable and no open regressions remain. Tests use JUnit 5 with Mockito. Name classes `*Test`, mirror source packages, prefer `@WebMvcTest` for controller slices, and use `@SpringBootTest` for cross-module scenarios. Run `mvn test` before each push and `mvn clean verify` ahead of reviews.

## Commit & Pull Request Guidelines
Follow Conventional Commits (e.g., `feat:`, `fix:`, `docs:`) in the imperative. Work on `feature/<meaningful_name>` branches off `develop`, rebase before PRs, and link issues plus concise change notes. Include relevant command output or curl snippets to document verification. Do not alter underlying frameworks without an approved ADR.

## Operational Boundaries
If you start a server, you are responsible for stopping or bouncing it; never manage servers inside containers. Do not remove containers, images, or files—rename to `deleted_{name}` when deprecation is required. Stay within backend Spring Boot responsibilities and request DBA assistance for any database infrastructure changes.

## Security & Configuration Tips
Store secrets in environment variables or a vault, rotate sample tokens in `src/main/resources/token_response*.json`, and confirm every new endpoint honors existing authentication and authorization policies before merge.
