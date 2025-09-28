# User Operational Rules

## Communication Rules
When the user mentions **KISSES/kisses** in their message, follow the _Keep It Short, Simple, Engaging, and Structured_ principle:
- Keep responses brief and direct
- Avoid unnecessary conversation or elaboration
- Be concise and to the point
- Structure responses clearly
- Stay engaging but minimal

When **KISSES/kisses** is NOT mentioned, provide full, detailed explanations in natural flow.

## Constitutional Framework Rules 🏛️
**CRITICAL CONSTITUTIONAL REQUIREMENT**: DO NOT change any underlying framework without ADR (Architecture Decision Record) approvals.

### Framework Changes Requiring ADR:
- ✋ Core frameworks (Spring Boot, DGS, etc.)
- ✋ Database frameworks (JPA, Hibernate)
- ✋ Security frameworks (Spring Security)
- ✋ GraphQL frameworks (DGS → Spring GraphQL)
- ✋ Build systems (Maven → Gradle)
- ✋ Testing frameworks (major versions)

### What Does NOT Require ADR:
- ✅ Adding dependencies within current frameworks
- ✅ Configuration changes
- ✅ Feature additions using existing frameworks
- ✅ Bug fixes and patches
- ✅ Minor version updates

## Server and Container Rules

### Server Management
- **If I start a server**: I have rights to stop or bounce it
- **Container servers**: DO NOT start/stop servers in any containers
- **Never remove**: Do not remove any containers and images

### File Management
- **Never delete files**: Do not delete any files period
- **If deletion needed**: Rename to `deleted_{name}` instead of deleting
- **Preserve data**: Always maintain file integrity

### Restrictions
- No container manipulation (start/stop/remove)
- No image removal
- No file deletion
- Use renaming for "deletion" operations

## Implementation
- Always check if operation involves containers before proceeding
- Use rename operations instead of delete commands
- Respect container lifecycle management restrictions
- Maintain data preservation principles
- **Constitutional compliance**: All framework changes require ADR approval first