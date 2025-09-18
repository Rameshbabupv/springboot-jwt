# Claude Development Guidelines

This file contains essential guidelines for Claude when working on this project to ensure constitutional compliance and best practices.

## 🚨 Constitutional Requirements (NON-NEGOTIABLE)

### GitFlow Workflow - MANDATORY
- **NEVER** commit directly to `main` branch
- **ALWAYS** create feature branches from `develop`
- **ALWAYS** follow the pattern: `feature/<meaningful-name>`
- **ALWAYS** test thoroughly before merging
- **ALWAYS** delete feature branch after successful merge

### Branch Structure
```
main (production-ready code only)
├── develop (integration branch)
    ├── feature/aop-logging
    ├── feature/jwt-auth
    ├── feature/database-entities
    └── feature/<next-feature>
```

### Required Commands Before ANY Development
```bash
# 1. Check current branch
git branch

# 2. Switch to develop (if not already)
git checkout develop

# 3. Pull latest changes
git pull origin develop

# 4. Create feature branch
git checkout -b feature/<meaningful-name>

# 5. Start development work
```

### Required Commands Before Committing
```bash
# 1. Run tests
mvn test

# 2. Run application to verify it starts
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 3. Check for linting/formatting issues (when available)
# mvn spotless:check

# 4. Stage and commit changes
git add .
git commit -m "feat: meaningful commit message

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>"
```

## 🔧 Technical Standards

### Build System
- **Maven** is the required build system (NOT Gradle for now)
- **DGS Framework** for GraphQL (version 8.1.1)
- **Spring Boot 3.2.0** with Java 17

### Testing Requirements
- **ALWAYS** run `mvn test` before committing
- **ALWAYS** ensure application starts successfully
- **TDD approach** preferred for new features
- **Test coverage** should not decrease

### Code Quality
- **Follow existing patterns** in the codebase
- **Use @Loggable annotation** for new methods requiring logging
- **Maintain consistent naming** conventions
- **Add documentation** for new features in `docs/features/`

## 📋 Development Checklist

Before starting ANY new feature:
- [ ] Confirm current branch is `develop`
- [ ] Pull latest changes from `develop`
- [ ] Create new feature branch: `feature/<name>`
- [ ] Update CLAUDE.md if new guidelines needed

Before committing ANY changes:
- [ ] Run `mvn test` (all tests pass)
- [ ] Run `mvn spring-boot:run -Dspring-boot.run.profiles=dev` (app starts)
- [ ] Verify no constitutional violations
- [ ] Stage and commit with proper message format

Before merging feature:
- [ ] All tests passing
- [ ] Feature fully implemented and tested
- [ ] Documentation updated
- [ ] Ready to merge to `develop`
- [ ] Delete feature branch after merge

## 🚨 Current Violation Alert

**STATUS**: ⚠️ AOP logging was implemented directly on branch `001-i-need-to` without following GitFlow

**REQUIRED FIX**:
1. Check git status
2. Create proper feature branch: `feature/aop-logging`
3. Move AOP changes to feature branch
4. Follow proper GitFlow process

## 📖 Quick Reference Commands

```bash
# GitFlow workflow
git checkout develop
git pull origin develop
git checkout -b feature/my-feature
# ... do development work ...
mvn test && mvn spring-boot:run -Dspring-boot.run.profiles=dev
git add . && git commit -m "feat: description"
git checkout develop
git merge feature/my-feature
git branch -d feature/my-feature
git push origin develop

# Testing commands
mvn test                                          # Run all tests
mvn spring-boot:run -Dspring-boot.run.profiles=dev  # Start in dev mode

# API testing
curl http://localhost:8080/api/hello
curl http://localhost:8080/api/health
curl -X POST http://localhost:8080/graphql -H "Content-Type: application/json" -d '{"query":"{ hello }"}'
```

## 📁 Project Structure Understanding

```
src/main/java/com/systech/nexus/
├── common/
│   ├── annotation/     # Custom annotations (@Loggable)
│   └── aspect/         # AOP aspects (LoggingAspect)
├── config/             # Configuration classes (AopConfig)
├── greeting/           # Feature modules
│   ├── controller/     # REST controllers
│   ├── domain/         # Domain models
│   ├── graphql/        # GraphQL resolvers
│   └── service/        # Business logic
└── NexusApplication.java
```

## 🎯 Next Development Priorities

1. **Fix GitFlow violation**: Move AOP to proper feature branch
2. **JWT Authentication**: Create `feature/jwt-auth` branch
3. **Database Entities**: Create `feature/database-entities` branch
4. **GraphQL Mutations**: Create `feature/graphql-mutations` branch

---

**Remember**: This file should be consulted before EVERY development task to ensure constitutional compliance!