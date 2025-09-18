# TDD Example: Live Demonstration

## 🔴 RED Phase - Tests Failing (Current State)

### Test Results:
```
[ERROR] COMPILATION ERROR:
Cannot find symbol:
- method getCustomGreeting(java.lang.String)
- method getGreetingWithTime()
```

### What Tests Expect:
1. ✅ `shouldReturnHelloWorldMessage()` - **PASSES** (already implemented)
2. ❌ `shouldReturnCustomGreeting()` - **FAILS** (method missing)
3. ❌ `shouldReturnGreetingWithTime()` - **FAILS** (method missing)
4. ❌ `shouldHandleHealthCheck()` - **FAILS** (endpoint missing)

## 🟢 GREEN Phase - Next Steps

To make tests pass, implement:

### 1. Add methods to HelloService:
```java
public Greeting getCustomGreeting(String name) {
    return new Greeting("Hello, " + name + "!");
}

public Greeting getGreetingWithTime() {
    return new Greeting("Hello, World! " + java.time.LocalDate.now());
}
```

### 2. Add health endpoint to HelloController:
```java
@GetMapping("/health")
public Map<String, String> health() {
    return Map.of("status", "UP");
}

@GetMapping("/hello/custom")
public Greeting customHello(@RequestParam String name) {
    return helloService.getCustomGreeting(name);
}
```

## 🔵 REFACTOR Phase - After Green

1. Extract constants
2. Improve error handling
3. Add validation
4. Keep tests passing

## 🎯 TDD Benefits Demonstrated

1. **Tests define behavior** before implementation
2. **Compilation errors** show missing methods clearly
3. **Small iterations** - implement one failing test at a time
4. **Confidence** - tests prove implementation works