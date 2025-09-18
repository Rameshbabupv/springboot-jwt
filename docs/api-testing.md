# API Testing Guide

## 🚀 Start Application
```bash
./gradlew bootRun
# Wait for: "Started NexusApplication in X.XXX seconds"
```

## 📡 Current Endpoints

### Hello World REST API
```bash
# Basic call
curl http://localhost:8080/api/hello

# Expected response:
{"message":"Hello, World!"}
```

```bash
# With status code
curl -w "\nStatus: %{http_code}\n" http://localhost:8080/api/hello

# Expected:
{"message":"Hello, World!"}
Status: 200
```

```bash
# Formatted output
curl -s http://localhost:8080/api/hello | jq .

# Expected:
{
  "message": "Hello, World!"
}
```

## 🗄️ Database Console
```bash
# Check H2 console (redirects to login)
curl -I http://localhost:8080/h2-console

# Expected: HTTP/1.1 302
```

**Web Access**: http://localhost:8080/h2-console
- **URL**: `jdbc:h2:mem:testdb`
- **User**: `sa`
- **Password**: `password`

## ✅ Health Checks
```bash
# App running check
curl -f http://localhost:8080/api/hello && echo "✅ API Working" || echo "❌ API Failed"

# Port check
lsof -i :8080 | grep LISTEN && echo "✅ Port 8080 Active" || echo "❌ Port Not Active"
```

## 🧪 Gradle Testing Commands
```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests HelloControllerTest

# Run tests matching pattern
./gradlew test --tests "*Controller*"

# Continuous testing (perfect for TDD)
./gradlew test --continuous

# Build with tests
./gradlew build

# Clean build
./gradlew clean build
```

## 🧪 Testing Script
```bash
#!/bin/bash
echo "🧪 Testing Nexus API..."
echo "1. Testing Hello endpoint:"
curl -s -w "Status: %{http_code}\n" http://localhost:8080/api/hello
echo "2. Testing H2 Console:"
curl -s -o /dev/null -w "H2 Status: %{http_code}\n" http://localhost:8080/h2-console
echo "✅ Tests completed!"
```

## 🚀 GraphQL Testing
```bash
# Start application with GraphQL (Maven)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Test GraphQL hello query
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"{ hello }"}'

# Expected response:
{"data":{"hello":"Hello, World!"}}
```

```bash
# Test GraphQL custom greeting query
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"{ customGreeting(name: \"Claude\") }"}'

# Expected response:
{"data":{"customGreeting":"Hello, Claude!"}}
```

**GraphiQL Web Interface**: http://localhost:8080/graphiql
- Interactive GraphQL query editor
- Schema exploration and documentation
- Query validation and auto-completion