# Application Constitution

## Non-Negotiable Principles

### 1. Architecture
- The application **MUST** be architected as a monolithic modular application
- The design **MUST** support the continuous addition of new functional modules
- Modules **MUST** be loosely coupled and independently deployable within the monolith

### 2. Data Management
- Initial local development setup **MUST** use an H2 in-memory database
- Production environments **MUST** transition to PostgreSQL
- Database migration strategy **MUST** be clearly defined and documented
- All data access **MUST** be abstracted through repository patterns

### 3. Authentication & Authorization
- All authentication and authorization **MUST** be handled through Keycloak integration
- No custom authentication mechanisms are permitted
- All endpoints **MUST** be secured through Keycloak policies
- Role-based access control **MUST** be implemented via Keycloak

### 4. Development Approach
- Development **MUST** start with minimal, functional endpoints
- Basic "hello world" endpoints **MUST** be established for both REST and GraphQL
- A stable foundation **MUST** be established before module expansion
- All new modules **MUST** follow the established patterns and principles
- Incremental development and testing **MUST** be prioritized over complex initial implementations

### 5. Git Workflow (GitFlow)
- The project **MUST** follow GitFlow workflow for all development
- All new features **MUST** be developed on feature branches: `feature/<meaningful-name>`
- Feature branches **MUST** be created from and merged back to `develop` branch
- The `main` branch **MUST** only contain production-ready code
- Feature branches **MUST** be deleted after successful merge and testing
- All commits **MUST** have clear, descriptive messages
- No direct commits to `main` or `develop` branches are permitted

## Compliance
These principles are non-negotiable and **MUST** be adhered to throughout the application lifecycle. Any deviation requires explicit justification and approval.