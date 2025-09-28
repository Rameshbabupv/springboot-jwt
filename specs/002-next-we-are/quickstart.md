# Company Master CRUD - Quickstart Guide

**Date**: 2025-09-27
**Feature**: Company Master CRUD Operations
**Audience**: Developers and QA testers

## Prerequisites

### Database Setup (DBA Task)
```sql
-- DBA must create companies table first
-- See data-model.md for complete DDL
```

### Authentication Required
- Valid JWT token with `app-admins` or `platform-admins` role
- Token must be included in `Authorization: Bearer <token>` header

### Test Environment
```bash
# 1. Start application
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 2. Verify GraphQL endpoint
curl http://localhost:8080/graphql -d '{"query": "{ __schema { queryType { name } } }"}'

# 3. Access GraphiQL (optional)
http://localhost:8080/graphiql
```

## Basic CRUD Operations

### 1. Create Company
```graphql
mutation CreateCompany {
  createCompany(input: {
    companyName: "Tech Solutions Inc"
    registrationNumber: "REG-2025-001"
    active: true
  }) {
    id
    companyName
    registrationNumber
    active
    createdDate
    createdBy
  }
}
```

**Expected Result**: New company created with auto-generated ID and audit fields

### 2. Query All Companies
```graphql
query GetAllCompanies {
  companies {
    id
    companyName
    registrationNumber
    active
    modifiedDate
  }
}
```

**Expected Result**: List of all companies (active and inactive)

### 3. Search Companies
```graphql
query SearchCompanies {
  companies(search: {
    companyName: "Tech"
    active: true
  }) {
    id
    companyName
    registrationNumber
  }
}
```

**Expected Result**: Companies matching search criteria

### 4. Get Specific Company
```graphql
query GetCompany {
  company(id: "1") {
    id
    companyName
    registrationNumber
    active
    createdDate
    modifiedDate
    createdBy
    modifiedBy
  }
}
```

**Expected Result**: Complete company details or null if not found

### 5. Update Company
```graphql
mutation UpdateCompany {
  updateCompany(id: "1", input: {
    companyName: "Tech Solutions International Inc"
  }) {
    id
    companyName
    modifiedDate
    modifiedBy
  }
}
```

**Expected Result**: Updated company with new modification timestamp

### 6. Disable Company (Soft Delete)
```graphql
mutation DisableCompany {
  disableCompany(id: "1") {
    id
    companyName
    active
    modifiedDate
    modifiedBy
  }
}
```

**Expected Result**: Company marked as inactive (active: false)

### 7. Reactivate Company
```graphql
mutation ReactivateCompany {
  reactivateCompany(id: "1") {
    id
    companyName
    active
    modifiedDate
  }
}
```

**Expected Result**: Company marked as active (active: true)

## Bulk Operations

### Bulk Import (Initial Data Loading)
```graphql
mutation BulkImport {
  bulkImportCompanies(input: {
    companies: [
      {
        companyName: "Company A Ltd"
        registrationNumber: "REG-2025-100"
      },
      {
        companyName: "Company B Corp"
        registrationNumber: "REG-2025-101"
      }
    ]
  }) {
    successCount
    failureCount
    errors
  }
}
```

**Expected Result**: Import summary with success/failure counts

## Error Scenarios

### 1. Duplicate Registration Number
```graphql
mutation CreateDuplicate {
  createCompany(input: {
    companyName: "Duplicate Test"
    registrationNumber: "REG-2025-001"  # Already exists
  }) {
    id
  }
}
```

**Expected Result**: GraphQL error about unique constraint violation

### 2. Invalid Input Validation
```graphql
mutation CreateInvalid {
  createCompany(input: {
    companyName: ""  # Empty name
    registrationNumber: "REG-2025-002"
  }) {
    id
  }
}
```

**Expected Result**: Validation error for required field

### 3. Unauthorized Access
```bash
# Request without JWT token
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query": "{ companies { id } }"}'
```

**Expected Result**: 401 Unauthorized or 403 Forbidden

### 4. Non-existent Company
```graphql
query GetNonExistent {
  company(id: "99999") {
    id
  }
}
```

**Expected Result**: null response (not an error)

## Integration Test Scenarios

### Test Sequence 1: Complete CRUD Flow
1. Create company → Verify creation
2. Update company → Verify changes
3. Search for company → Verify findability
4. Disable company → Verify soft delete
5. Reactivate company → Verify restoration

### Test Sequence 2: Validation & Security
1. Attempt creation without auth → Verify rejection
2. Create with invalid data → Verify validation
3. Attempt duplicate registration → Verify uniqueness

### Test Sequence 3: Bulk Operations
1. Bulk import valid data → Verify success
2. Bulk import with some invalid → Verify partial success
3. Query imported companies → Verify visibility

## Performance Verification

### Load Test Queries
```bash
# Simple load test (requires auth token)
for i in {1..100}; do
  curl -X POST http://localhost:8080/graphql \
    -H "Authorization: Bearer $JWT_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{"query": "{ companies { id companyName } }"}' &
done
wait
```

**Expected Result**: All requests complete within reasonable time (<2s each)

## Monitoring & Debugging

### Application Logs
- Check for SQL queries execution
- Verify JWT token validation
- Monitor audit field population

### Database Verification
```sql
-- Verify data integrity
SELECT id, company_name, registration_number, active, created_by
FROM companies
ORDER BY created_date DESC
LIMIT 10;
```

## Success Criteria Checklist

- [ ] All CRUD operations work with valid authentication
- [ ] Validation prevents invalid data entry
- [ ] Registration numbers enforce uniqueness
- [ ] Audit trail captures user and timestamp information
- [ ] Search functionality returns correct results
- [ ] Unauthorized requests are properly rejected
- [ ] Bulk import processes multiple records correctly
- [ ] Soft delete preserves data for audit purposes