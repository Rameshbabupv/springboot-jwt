# Framework Guide

## 🏗️ Architecture (Simple)
```
Spring Boot 3.2.0 + Gradle + H2 Database + DGS GraphQL
```

## 📦 Package Structure
```
com.systech.nexus/
├── greeting/                  # Feature modules
│   ├── domain/               # Models (Greeting.java)
│   ├── service/              # Business logic (HelloService.java)
│   └── controller/           # REST endpoints (HelloController.java)
└── NexusApplication.java     # Main app
```

## 🎯 Core Principles
1. **Modular** - Each feature in its own package
2. **Simple** - Minimal dependencies, clear structure
3. **Constitutional** - Follow project constitution rules

## 🛠️ Tech Stack
- **Java 17** + **Spring Boot 3.2.0**
- **Gradle** for build with **DGS Framework**
- **H2 Database** (dev) → **PostgreSQL** (prod)
- **JUnit 5** for testing
- **GraphQL** via **DGS** (Netflix)

## ⚡ Quick Commands
```bash
./gradlew bootRun            # Start app
./gradlew test               # Run all tests
./gradlew build              # Build JAR
./gradlew clean build        # Clean build
```

## 🔄 Development Flow
1. **Test First** (TDD) - Write failing test
2. **Code** - Implement minimal solution
3. **Verify** - Run tests, ensure pass
4. **Document** - Update docs if needed