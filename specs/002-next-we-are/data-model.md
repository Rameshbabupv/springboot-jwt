# Data Model: Company Master

**Date**: 2025-09-27
**Feature**: Company Master CRUD Operations

## Entity: Company

### Database Table: companies
**Request for DBA**: Please create the following table structure

```sql
CREATE TABLE companies (
    id BIGSERIAL PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    registration_number VARCHAR(100) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT true,
    created_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    modified_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255) NOT NULL,
    modified_by VARCHAR(255) NOT NULL
);

CREATE INDEX idx_companies_name ON companies(company_name);
CREATE INDEX idx_companies_reg_number ON companies(registration_number);
CREATE INDEX idx_companies_active ON companies(active);
```

### JPA Entity Mapping

```java
@Entity
@Table(name = "companies")
@EntityListeners(AuditingEntityListener.class)
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Company name is required")
    @Size(max = 255, message = "Company name must not exceed 255 characters")
    @Column(name = "company_name", nullable = false)
    private String companyName;

    @NotBlank(message = "Registration number is required")
    @Size(max = 100, message = "Registration number must not exceed 100 characters")
    @Column(name = "registration_number", nullable = false, unique = true)
    private String registrationNumber;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant createdDate;

    @LastModifiedDate
    @Column(name = "modified_date", nullable = false)
    private Instant modifiedDate;

    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "modified_by", nullable = false)
    private String modifiedBy;
}
```

## Validation Rules

### Business Rules
1. **Company Name**: Required, non-blank, max 255 characters
2. **Registration Number**: Required, non-blank, unique across system, max 100 characters
3. **Active Status**: Defaults to true, required field
4. **Audit Fields**: Automatically managed by Spring Data JPA auditing

### Data Integrity
- Registration number uniqueness enforced at database level
- All audit fields required and automatically populated
- Soft delete via active flag (never actually delete records)

## State Transitions

### Company Lifecycle
```
Created (active=true) → Active Operations → Disabled (active=false)
                                      ↗
                              Can be reactivated
```

### Allowed Operations by State
- **Active (active=true)**: All CRUD operations allowed
- **Inactive (active=false)**: Read operations only, can be reactivated

## Relationships
- **Current Scope**: Standalone entity (no foreign key relationships)
- **Future Considerations**: May relate to Users, Locations, or other business entities

## Search and Filtering

### Supported Search Criteria
1. **By Company Name**: Case-insensitive partial match
2. **By Registration Number**: Exact match
3. **By Active Status**: Filter active/inactive companies
4. **Combined Search**: Multiple criteria with AND logic

### Index Strategy
- Primary key index (automatic)
- Company name index for search performance
- Registration number unique index
- Active status index for filtering