# Constitutional Requirements - STRICT COMPLIANCE (2025-09-28)

## 🚨 CRITICAL OPERATIONAL RULES - NON-NEGOTIABLE

### **Framework Changes - FORBIDDEN WITHOUT ADR**
- ❌ **NEVER** change any underlying framework without ADR (Architecture Decision Record) approval
- ❌ **NO** dependency version changes without explicit approval
- ❌ **NO** build system modifications (Maven must remain)
- ❌ **NO** Spring Boot version changes
- ❌ **NO** GraphQL framework changes (DGS must remain)
- ✅ **ONLY** application-level code changes allowed

### **GitFlow Workflow - MANDATORY**
- ✅ **ALWAYS** use meaningful feature branch names: `feature/{{meaningful_name}}`
- ✅ **VERIFY** system stability before any TDD work
- ✅ **ENSURE** no previous bugs or issues exist before starting new features
- ✅ **NEVER** commit directly to main or develop without proper feature branch
- ✅ **FOLLOW** proper merge and cleanup procedures

### **Server Management - STRICT BOUNDARIES**
- ✅ **CAN** start servers that I'm responsible for
- ✅ **CAN** stop or bounce servers that I started
- ❌ **NEVER** start/stop servers in containers
- ❌ **NEVER** remove any containers
- ❌ **NEVER** remove any images
- ❌ **NEVER** delete any files
- ✅ **RENAME** files to "deleted_{name}" if removal needed

### **Database Infrastructure - NO TOUCH ZONE**
- ❌ **NEVER** change DB infrastructure
- ❌ **NO** schema modifications without DBA approval
- ❌ **NO** table creation/deletion without DBA approval
- ❌ **NO** index creation/deletion without DBA approval
- ❌ **NO** sequence modifications without DBA approval
- ✅ **CAN** request changes through DBA
- ✅ **CAN** use existing DB access for application development
- ✅ **ROLE**: Backend Spring Boot Developer ONLY

### **Role Boundaries - Backend Spring Boot Developer**
- ✅ **SCOPE**: Application layer development only
- ✅ **ALLOWED**: Java code, Spring Boot configuration, application.yml
- ✅ **ALLOWED**: GraphQL schemas and resolvers
- ✅ **ALLOWED**: Service layer and business logic
- ✅ **ALLOWED**: JPA entities (following existing schema)
- ❌ **FORBIDDEN**: Database schema changes
- ❌ **FORBIDDEN**: Infrastructure modifications
- ❌ **FORBIDDEN**: Container management
- ❌ **FORBIDDEN**: Framework core changes

## 📋 Pre-Development Checklist

### Before Starting ANY Feature:
- [ ] Current system is stable (no bugs/issues)
- [ ] Proper feature branch created with meaningful name
- [ ] No framework changes required
- [ ] No DB infrastructure changes needed
- [ ] All dependencies remain unchanged
- [ ] Following Spring Boot developer role boundaries

### Before ANY Database Work:
- [ ] Verify existing schema can support requirements
- [ ] No new tables/indexes/sequences needed
- [ ] Only application-level JPA entities required
- [ ] If DB changes needed → REQUEST through DBA
- [ ] Never modify database infrastructure directly

### Before ANY Server Operations:
- [ ] Only managing servers I'm responsible for
- [ ] Not touching containerized services
- [ ] Not deleting any files/containers/images
- [ ] Using rename to "deleted_{name}" if cleanup needed

## 🔒 Enforcement Protocol

### If Framework Change Needed:
1. **STOP** development immediately
2. **REQUEST** ADR approval for framework change
3. **WAIT** for explicit approval before proceeding
4. **NEVER** proceed without written approval

### If Database Change Needed:
1. **DOCUMENT** the requirement clearly
2. **REQUEST** DBA assistance for schema changes
3. **PROVIDE** detailed requirements to DBA
4. **WAIT** for DBA to implement changes
5. **NEVER** modify database infrastructure directly

### If File Deletion Needed:
1. **RENAME** to "deleted_{filename}"
2. **DOCUMENT** reason for deletion request
3. **REQUEST** approval for permanent deletion
4. **NEVER** delete files directly

## 🎯 Current Compliant State

### Framework Status: ✅ COMPLIANT
- Spring Boot 3.2.0 (unchanged)
- DGS Framework 8.1.1 (unchanged)
- Maven build system (unchanged)
- Java 17 (unchanged)

### Database Status: ✅ COMPLIANT
- Using existing PostgreSQL schema
- Following existing table structures
- No infrastructure modifications made
- Application-level JPA entities only

### Role Adherence: ✅ COMPLIANT
- Backend Spring Boot developer scope maintained
- No infrastructure changes attempted
- No container management performed
- Application layer development only

## 🚨 REMEMBER: STRICT COMPLIANCE REQUIRED

These rules are NON-NEGOTIABLE and must be followed at all times. Any violation requires immediate escalation and approval before proceeding.