# Nexus Application Documentation

## 📁 Documentation Structure

```
docs/
├── README.md                        # This file - main navigation
├── framework.md                     # Simple framework guide
├── api-testing.md                   # Curl commands and testing
├── tdd-workflow.md                  # Test-driven development guide
├── jwt-authentication-testing.md    # JWT testing guide
└── features/                        # Feature-specific documentation
    ├── hello-world.md               # Hello world implementation
    ├── aop-logging.md               # AOP logging feature
    └── jwt-authentication.md        # JWT authentication feature
```

## 🎯 Purpose
Keep **short, simple, and practical** documentation for ongoing development.

## 📚 Quick Navigation

### Core Guides
- **[Framework Guide](framework.md)** - Simple architecture overview
- **[API Testing](api-testing.md)** - Curl commands and testing examples
- **[TDD Workflow](tdd-workflow.md)** - Test-first development process

### Security & Authentication
- **[JWT Authentication Testing](jwt-authentication-testing.md)** - Complete JWT testing guide
- **[JWT Authentication Feature](features/jwt-authentication.md)** - Feature implementation details

### Features
- **[Hello World Feature](features/hello-world.md)** - First implementation details
- **[AOP Logging Feature](features/aop-logging.md)** - Aspect-oriented programming logging

## 🔄 Development Process
1. Write failing tests first (TDD)
2. Implement minimal code to pass tests
3. Run tests to verify success
4. Document new features
5. Refactor and improve