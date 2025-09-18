# AOP Logging Implementation

## Overview

This application implements comprehensive Aspect-Oriented Programming (AOP) logging using Spring AOP and custom annotations for cross-cutting concerns.

## Features

### 🔍 Correlation ID Tracking
- Each request gets a unique 8-character correlation ID
- Enables request tracing across multiple service calls
- Stored in MDC (Mapped Diagnostic Context) for thread-safe logging

### ⏱️ Performance Monitoring
- Execution time tracking with millisecond precision
- Entry and exit logging for method calls
- Exception handling with execution time on failure

### 📝 Configurable Logging
- Custom `@Loggable` annotation for selective logging
- Parameter logging (configurable on/off)
- Return value logging (configurable on/off)
- Custom method descriptions for better log readability

## Implementation Details

### 1. Custom Annotation: `@Loggable`

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
    boolean logParameters() default true;
    boolean logResult() default true;
    boolean logExecutionTime() default true;
    String description() default "";
}
```

**Usage Examples:**
```java
// Method-level logging with custom description
@Loggable(description = "Get hello world message")
public Greeting hello() { ... }

// Class-level logging for all methods
@Service
@Loggable(description = "Greeting Service Operations")
public class HelloService { ... }

// Selective logging (no parameters for health check)
@Loggable(logParameters = false, description = "Health check endpoint")
public Map<String, String> health() { ... }
```

### 2. LoggingAspect Implementation

**Key Features:**
- Around advice for complete method lifecycle logging
- Automatic correlation ID generation and MDC management
- Exception logging with execution time
- Result truncation for large responses (>200 chars)
- Thread-safe logging using correlation IDs

**Log Format:**
```
[correlationId] → ENTERING: className.methodName() - description | Parameters: [params]
[correlationId] ← EXITING: className.methodName() | Execution time: Xms | Result: result
[correlationId] ✗ EXCEPTION in: className.methodName() | Execution time: Xms | Exception: ExceptionType | Message: error message
```

### 3. Configuration

**AOP Configuration:**
```java
@Configuration
@EnableAspectJAutoProxy
public class AopConfig {
}
```

**Maven Dependency:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

## Log Output Examples

### REST API Call
```
[da9fd225] → ENTERING: com.systech.nexus.greeting.controller.HelloController.hello() - Get hello world message
[90e7f150] → ENTERING: com.systech.nexus.greeting.service.HelloService.getHelloMessage() - Greeting Service Operations
[90e7f150] ← EXITING: com.systech.nexus.greeting.service.HelloService.getHelloMessage() | Execution time: 0ms | Result: com.systech.nexus.greeting.domain.Greeting@42431e12
[da9fd225] ← EXITING: com.systech.nexus.greeting.controller.HelloController.hello() | Execution time: 1ms | Result: com.systech.nexus.greeting.domain.Greeting@42431e12
```

### GraphQL Query
```
[5c026bb1] → ENTERING: com.systech.nexus.greeting.graphql.HelloDataFetcher.hello() - GraphQL hello query
[ab25a7cb] → ENTERING: com.systech.nexus.greeting.service.HelloService.getHelloMessage() - Greeting Service Operations
[ab25a7cb] ← EXITING: com.systech.nexus.greeting.service.HelloService.getHelloMessage() | Execution time: 0ms | Result: com.systech.nexus.greeting.domain.Greeting@590dada2
[5c026bb1] ← EXITING: com.systech.nexus.greeting.graphql.HelloDataFetcher.hello() | Execution time: 0ms | Result: Hello, World!
```

### Custom Greeting with Parameters
```
[cefd9262] → ENTERING: com.systech.nexus.greeting.controller.HelloController.customHello() - Get custom greeting message | Parameters: [John]
[d2d8abe4] → ENTERING: com.systech.nexus.greeting.service.HelloService.getCustomGreeting() - Greeting Service Operations | Parameters: [John]
[d2d8abe4] ← EXITING: com.systech.nexus.greeting.service.HelloService.getCustomGreeting() | Execution time: 0ms | Result: com.systech.nexus.greeting.domain.Greeting@608398ee
[cefd9262] ← EXITING: com.systech.nexus.greeting.controller.HelloController.customHello() | Execution time: 0ms | Result: com.systech.nexus.greeting.domain.Greeting@608398ee
```

## Benefits

### 🎯 Cross-Cutting Concerns
- Separation of logging logic from business code
- Centralized logging configuration
- Consistent logging format across the application

### 🚀 Performance Monitoring
- Method execution time tracking
- Performance bottleneck identification
- Request flow analysis

### 🔍 Debugging & Troubleshooting
- Complete request tracing with correlation IDs
- Parameter and result logging for debugging
- Exception tracking with context

### 🛠️ Maintenance
- Easy to add/remove logging from methods
- Configurable logging levels per method
- No code pollution with logging statements

## Testing AOP Logging

```bash
# Test REST endpoints
curl http://localhost:8080/api/hello
curl "http://localhost:8080/api/hello/custom?name=John"
curl http://localhost:8080/api/health

# Test GraphQL
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"{ hello }"}'

curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"{ customGreeting(name: \"Alice\") }"}'
```

Monitor the application logs to see the AOP logging in action with correlation IDs, execution times, and method flow tracking.