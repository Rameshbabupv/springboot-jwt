# TDD Workflow Guide

## 🔄 TDD Cycle (Red → Green → Refactor)

### 1. 🔴 RED: Write Failing Test
```bash
# Create test first - it MUST fail
./gradlew test
# Expected: Test failures showing what needs to be implemented
```

### 2. 🟢 GREEN: Make Test Pass
```bash
# Write minimal code to pass the test
./gradlew test
# Expected: All tests pass
```

### 3. 🔵 REFACTOR: Improve Code
```bash
# Clean up code, maintain passing tests
./gradlew test
# Expected: Tests still pass after refactoring
```

## ⚡ Quick Commands

### Run All Tests
```bash
./gradlew test
```

### Run Specific Test Class
```bash
./gradlew test --tests HelloControllerTest
```

### Run Tests with Detailed Output
```bash
./gradlew test --info
```

### Skip Tests (Build Only)
```bash
./gradlew build -x test
```

## 📊 Test Results Interpretation

### ✅ Success Output
```
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### ❌ Failure Output
```
Tests run: 3, Failures: 1, Errors: 0, Skipped: 0
[ERROR] COMPILATION ERROR
[ERROR] Failed to execute goal
```

## 🎯 TDD Best Practices
1. **Write smallest failing test** first
2. **Write minimal code** to pass
3. **Refactor** while keeping tests green
4. **Test behavior**, not implementation
5. **Keep tests fast** and independent

## 📁 Test Structure
```
src/test/java/com/systech/nexus/
├── greeting/
│   ├── controller/    # Controller tests
│   ├── service/       # Service tests
│   └── domain/        # Model tests
└── integration/       # Integration tests
```