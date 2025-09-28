# Constitutional Framework Rules

## Framework Change Governance

### Constitutional Rule: NO Framework Changes Without ADR
**CRITICAL**: DO NOT change any underlying framework without ADR (Architecture Decision Record) approvals.

This is a **constitutional requirement** that overrides all other considerations.

### What Constitutes Framework Changes:
- Core application frameworks (Spring Boot, DGS, etc.)
- Database frameworks (JPA, Hibernate)
- Security frameworks (Spring Security)
- GraphQL frameworks (DGS → Spring GraphQL)
- Build systems (Maven → Gradle)
- Testing frameworks (JUnit versions)

### Required Process:
1. **Identify framework change need**
2. **Create ADR document** with:
   - Current state analysis
   - Proposed change rationale
   - Impact assessment
   - Migration strategy
   - Risk analysis
3. **Get formal approval** through ADR process
4. **Only then proceed** with implementation

### Examples of What Requires ADR:
- ✋ Switching from DGS Framework to Spring GraphQL
- ✋ Changing from Maven to Gradle
- ✋ Upgrading major framework versions
- ✋ Replacing Spring Security with custom auth
- ✋ Database framework changes

### What Does NOT Require ADR:
- ✅ Adding new dependencies within current frameworks
- ✅ Configuration changes
- ✅ Feature additions using existing frameworks
- ✅ Bug fixes and patches
- ✅ Minor version updates

## Enforcement:
- This rule is **constitutional** and non-negotiable
- All framework changes must follow ADR governance
- No exceptions without explicit constitutional amendment