# Constitutional Developer Role - Database Access Clarification (2025-09-28)

## 🚨 CRITICAL ROLE BOUNDARY CLARIFICATION

### **Database Access Rights - CLARIFIED**
- ✅ **CAN** use existing database and schema (`nexus_hrms`)
- ✅ **CAN** read from existing tables with populated data
- ✅ **CAN** insert/update/delete application data through JPA
- ❌ **CANNOT** modify database structure (tables, schemas, sequences)
- ❌ **CANNOT** create new databases or test databases
- ❌ **CANNOT** use DDL operations (CREATE, ALTER, DROP)

### **Test Configuration - CORRECTED**
- **Database**: Use same `nexus_hrms` database (not separate test DB)
- **Schema**: Use existing `nx_core` schema with populated data
- **DDL Mode**: `validate` (verify existing structure, no modifications)
- **Data**: Work with existing populated tables

### **Previous Incorrect Assumptions**
- ❌ Creating separate test database (`nexus_hrms_test`)
- ❌ Using `create-drop` to recreate schema
- ❌ Modifying database structure for tests
- ❌ Creating tables, sequences, or schemas

### **Correct Developer Approach**
- ✅ Use existing database infrastructure
- ✅ Work with populated data through application layer
- ✅ Validate that entity mapping matches existing schema
- ✅ Test business logic with existing data

### **Configuration Updates Made**
- **Test Database**: Changed to use `nexus_hrms` (existing)
- **DDL Mode**: Changed to `validate` (no structure changes)
- **Schema**: Use existing `nx_core` with populated tables

### **Role Compliance Checklist**
- [ ] No database structure modifications
- [ ] No DDL operations
- [ ] Use existing populated schema
- [ ] Work through JPA application layer only
- [ ] Request DBA for any structural changes needed

## **Backend Developer Scope - CONFIRMED**
- **Application Layer**: Full access and modification rights
- **Data Layer**: Read/write through JPA to existing structures
- **Database Layer**: READ-ONLY access to structure, NO modifications
- **DBA Requests**: Required for any structural changes