# GraphQL DGS Framework Implementation

## Current Project Status
- **Framework**: Spring Boot 3.2.0 with Maven (Gradle migration attempted but had wrapper corruption issues)
- **Profile**: Running in dev mode with `mvn spring-boot:run -Dspring-boot.run.profiles=dev`
- **Existing REST**: Hello World API working at `/api/hello` and `/api/health`

## DGS Framework Research Results
- **DGS Framework**: Netflix's GraphQL server framework for Spring Boot
- **Key Features**: Annotation-based programming model, test framework, Spring Security integration
- **Endpoints**: 
  - GraphQL endpoint: `/graphql`
  - GraphiQL UI: `/graphiql`
  - Subscriptions: `/subscriptions`

## Implementation Plan
1. Add DGS dependencies to Maven pom.xml
2. Create GraphQL schema file (hello.graphqls)
3. Implement GraphQL resolver with @DgsComponent and @DgsQuery
4. Test GraphQL endpoint functionality
5. Update documentation

## Next Steps
- Need to research specific Maven dependencies for DGS Framework
- Create schema-first GraphQL implementation
- Add simple hello world query to match existing REST functionality