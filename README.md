# Spring Boot JWT with GraphQL - Nexus Application

A modular Spring Boot application implementing both REST and GraphQL APIs with JWT authentication support.

## 🚀 Quick Start

```bash
# Start the application in dev mode
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Wait for: "Started NexusApplication in X.XXX seconds"
```

## 📡 Available Endpoints

### REST API
- `GET /api/hello` → `{"message":"Hello, World!"}`
- `GET /api/health` → `{"status":"UP"}`
- `GET /h2-console` → H2 Database Console

### GraphQL API
- `POST /graphql` → GraphQL endpoint
- `GET /graphiql` → Interactive GraphQL query editor

### GraphQL Queries
```graphql
# Simple hello query
{
  hello
}

# Custom greeting query
{
  customGreeting(name: "YourName")
}
```

## 🛠️ Tech Stack

- **Java 17** + **Spring Boot 3.2.0**
- **Maven** for build management
- **DGS Framework 8.1.1** for GraphQL
- **H2 Database** (dev) → **PostgreSQL** (production)
- **JUnit 5** for testing

## 🏗️ Architecture

```
com.systech.nexus/
├── greeting/                  # Feature modules
│   ├── domain/               # Models (Greeting.java)
│   ├── service/              # Business logic (HelloService.java)
│   ├── controller/           # REST endpoints (HelloController.java)
│   └── graphql/              # GraphQL resolvers (HelloDataFetcher.java)
└── NexusApplication.java     # Main application
```

## 🧪 Testing

```bash
# Run all tests
mvn test

# Test REST endpoint
curl http://localhost:8080/api/hello | jq .

# Test GraphQL endpoint
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"{ hello }"}'

# Expected GraphQL response:
# {"data":{"hello":"Hello, World!"}}
```

## 📋 Development Workflow (GitFlow)

This project follows **GitFlow** workflow:

1. **Create feature branch**: `git checkout -b feature/your-feature-name develop`
2. **Develop and test**: Make changes, write tests, ensure all pass
3. **Commit changes**: Use descriptive commit messages
4. **Merge to develop**: `git checkout develop && git merge feature/your-feature-name`
5. **Delete feature branch**: `git branch -d feature/your-feature-name`

### Branch Structure
- `main` - Production-ready code only
- `develop` - Integration branch for features
- `feature/*` - Individual feature development

## 📖 Documentation

- **[API Testing Guide](docs/api-testing.md)** - Detailed testing examples
- **[Framework Guide](docs/framework.md)** - Architecture overview
- **[TDD Workflow](docs/tdd-workflow.md)** - Test-driven development guide
- **[Constitution](CONSTITUTION.md)** - Non-negotiable project principles

## 🎯 Next Steps

- Implement Keycloak authentication
- Add database entities and repositories
- Implement GraphQL mutations
- Add GraphQL subscriptions
- Integrate with PostgreSQL for production

## 📄 License

This project follows the constitutional requirements defined in [CONSTITUTION.md](CONSTITUTION.md).