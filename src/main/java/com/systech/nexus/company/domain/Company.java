package com.systech.nexus.company.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Company entity representing a company master record in the system.
 *
 * MAJOR CHANGES:
 * v1.0 (2025-09-27) - Initial implementation matching actual PostgreSQL database structure
 * v1.1 (2025-09-28) - Complete DAO with ALL non-null columns from company_master table
 *
 * Database Details:
 * - Table: company_master (PostgreSQL nx_core schema)
 * - Primary Key: company_id (sequence-generated)
 * - Required Foreign Key: registered_country_id (references country_master)
 * - Unique Constraints: company_code, primary_email (conditional)
 * - Check Constraints: company_status, company_type, financial months, etc.
 *
 * Phase 1 Implementation:
 * - Contains ALL non-null columns from actual database table
 * - User input fields: company_code, company_name, company_short_name, registered_address, primary_email
 * - Required foreign key: registered_country_id
 * - Database defaults: All other non-null fields have database-level defaults
 * - Audit fields: created_at, modified_at (auto-populated by database triggers)
 *
 * Business Rules:
 * - Company code must be unique across all companies
 * - Primary email must be unique for ACTIVE companies only
 * - Financial year months must be valid (1-12) and different
 * - Employee and user limits must be positive
 * - Company status restricted to: ACTIVE, INACTIVE, SUSPENDED, TERMINATED
 * - Company type restricted to: PRIVATE_LIMITED, PUBLIC_LIMITED, etc.
 *
 * @author Backend Developer
 * @version 1.1
 * @since 1.0
 */
@Entity
@Table(name = "company_master", schema = "nx_core")
@EntityListeners(AuditingEntityListener.class)
public class Company {

    /**
     * Primary key for the company entity.
     * Maps to company_id column in the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "company_seq")
    @SequenceGenerator(name = "company_seq", sequenceName = "nx_core.company_id_seq", allocationSize = 1)
    @Column(name = "company_id")
    private Long id;

    // === USER INPUT REQUIRED FIELDS ===

    /**
     * Unique company code (required user input).
     * Used for company identification and internal references.
     */
    @Column(name = "company_code", nullable = false, unique = true, length = 20)
    @NotBlank(message = "Company code is required")
    @Size(max = 20, message = "Company code must not exceed 20 characters")
    private String companyCode;

    /**
     * Company name (required user input).
     * Primary display name for the company.
     */
    @Column(name = "company_name", nullable = false, length = 200)
    @NotBlank(message = "Company name is required")
    @Size(max = 200, message = "Company name must not exceed 200 characters")
    private String companyName;

    /**
     * Company short name (required user input).
     * Abbreviated version for display purposes.
     */
    @Column(name = "company_short_name", nullable = false, length = 50)
    @NotBlank(message = "Company short name is required")
    @Size(max = 50, message = "Company short name must not exceed 50 characters")
    private String companyShortName;

    /**
     * Registered address (required user input).
     * Legal registered address of the company.
     */
    @Column(name = "registered_address", nullable = false, columnDefinition = "text")
    @NotBlank(message = "Registered address is required")
    private String registeredAddress;

    /**
     * Primary email (required user input).
     * Primary contact email address for the company.
     */
    @Column(name = "primary_email", nullable = false, length = 150)
    @NotBlank(message = "Primary email is required")
    @Email(message = "Primary email should be valid")
    @Size(max = 150, message = "Primary email must not exceed 150 characters")
    private String primaryEmail;

    // === REQUIRED FOREIGN KEY ===

    /**
     * Registered country ID (required foreign key).
     * References country_master table for the country where company is registered.
     */
    @Column(name = "registered_country_id", nullable = false)
    @NotNull(message = "Registered country ID is required")
    private Long registeredCountryId;

    // === FIELDS WITH DATABASE DEFAULTS ===

    /**
     * Company type (has database default: PRIVATE_LIMITED).
     * Legal structure of the company.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "company_type", nullable = false, length = 20)
    private CompanyType companyType = CompanyType.PRIVATE_LIMITED;

    /**
     * Financial year start month (has database default: 4 = April).
     * Month when financial year begins (1-12).
     */
    @Column(name = "financial_year_start_month", nullable = false)
    private Integer financialYearStartMonth = 4;

    /**
     * Financial year end month (has database default: 3 = March).
     * Month when financial year ends (1-12).
     */
    @Column(name = "financial_year_end_month", nullable = false)
    private Integer financialYearEndMonth = 3;

    /**
     * Default currency code (has database default: INR).
     * ISO currency code for company transactions.
     */
    @Column(name = "default_currency_code", nullable = false, length = 3)
    private String defaultCurrencyCode = "INR";

    /**
     * Default timezone (has database default: Asia/Kolkata).
     * Timezone for company operations.
     */
    @Column(name = "default_timezone", nullable = false, length = 50)
    private String defaultTimezone = "Asia/Kolkata";

    /**
     * Default language (has database default: en).
     * Primary language for company interface.
     */
    @Column(name = "default_language", nullable = false, length = 10)
    private String defaultLanguage = "en";

    /**
     * Date format (has database default: DD-MM-YYYY).
     * Preferred date display format.
     */
    @Column(name = "date_format", nullable = false, length = 20)
    private String dateFormat = "DD-MM-YYYY";

    /**
     * Time format (has database default: 24_HOUR).
     * Preferred time display format.
     */
    @Column(name = "time_format", nullable = false, length = 10)
    private String timeFormat = "24_HOUR";

    /**
     * Multi-location flag (has database default: false).
     * Whether company operates in multiple locations.
     */
    @Column(name = "is_multi_location", nullable = false)
    private Boolean isMultiLocation = false;

    /**
     * Multi-currency flag (has database default: false).
     * Whether company handles multiple currencies.
     */
    @Column(name = "is_multi_currency", nullable = false)
    private Boolean isMultiCurrency = false;

    /**
     * Subscription plan (has database default: STANDARD).
     * Current subscription tier.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_plan", nullable = false, length = 50)
    private SubscriptionPlan subscriptionPlan = SubscriptionPlan.STANDARD;

    /**
     * License type (has database default: PERPETUAL).
     * Type of software license.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "license_type", nullable = false, length = 20)
    private LicenseType licenseType = LicenseType.PERPETUAL;

    /**
     * Company status (has database default: ACTIVE).
     * Current operational status of the company.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "company_status", nullable = false, length = 20)
    private CompanyStatus companyStatus = CompanyStatus.ACTIVE;

    /**
     * Trial flag (has database default: false).
     * Whether company is on trial.
     */
    @Column(name = "is_trial", nullable = false)
    private Boolean isTrial = false;

    /**
     * Demo flag (has database default: false).
     * Whether company is demo account.
     */
    @Column(name = "is_demo", nullable = false)
    private Boolean isDemo = false;

    /**
     * Biometric integration flag (has database default: false).
     * Whether biometric features are enabled.
     */
    @Column(name = "enable_biometric_integration", nullable = false)
    private Boolean enableBiometricIntegration = false;

    /**
     * Mobile app flag (has database default: true).
     * Whether mobile app access is enabled.
     */
    @Column(name = "enable_mobile_app", nullable = false)
    private Boolean enableMobileApp = true;

    /**
     * Self service flag (has database default: true).
     * Whether self-service features are enabled.
     */
    @Column(name = "enable_self_service", nullable = false)
    private Boolean enableSelfService = true;

    /**
     * Workflow approvals flag (has database default: true).
     * Whether workflow approval system is enabled.
     */
    @Column(name = "enable_workflow_approvals", nullable = false)
    private Boolean enableWorkflowApprovals = true;

    /**
     * Audit logging flag (has database default: true).
     * Whether audit logging is enabled.
     */
    @Column(name = "enable_audit_logging", nullable = false)
    private Boolean enableAuditLogging = true;

    /**
     * Data encryption flag (has database default: true).
     * Whether data encryption is enabled.
     */
    @Column(name = "enable_data_encryption", nullable = false)
    private Boolean enableDataEncryption = true;

    // === AUDIT FIELDS (AUTO-POPULATED) ===

    /**
     * Timestamp when the company record was created (auto-populated).
     * Managed by database triggers.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the company record was last modified (auto-populated).
     * Managed by database triggers.
     */
    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;

    // === ENUMS ===

    /**
     * Enum for company status values.
     */
    public enum CompanyStatus {
        ACTIVE, INACTIVE, SUSPENDED, TERMINATED
    }

    /**
     * Enum for company type values.
     */
    public enum CompanyType {
        PRIVATE_LIMITED, PUBLIC_LIMITED, PARTNERSHIP, PROPRIETORSHIP, LLP, NGO, GOVERNMENT
    }

    /**
     * Enum for subscription plan values.
     */
    public enum SubscriptionPlan {
        BASIC, STANDARD, PREMIUM, ENTERPRISE
    }

    /**
     * Enum for license type values.
     */
    public enum LicenseType {
        PERPETUAL
    }

    // === CONSTRUCTORS ===

    /**
     * Default no-args constructor required by JPA.
     */
    public Company() {}

    /**
     * Constructor for creating a new company with required user input fields.
     * All other non-null fields will use database defaults.
     *
     * @param companyCode          the unique company code
     * @param companyName          the name of the company
     * @param companyShortName     the short name of the company
     * @param registeredAddress    the registered address
     * @param primaryEmail         the primary email address
     * @param registeredCountryId  the country where company is registered
     */
    public Company(String companyCode, String companyName, String companyShortName,
                   String registeredAddress, String primaryEmail, Long registeredCountryId) {
        this.companyCode = companyCode;
        this.companyName = companyName;
        this.companyShortName = companyShortName;
        this.registeredAddress = registeredAddress;
        this.primaryEmail = primaryEmail;
        this.registeredCountryId = registeredCountryId;
        // All other fields use defaults defined above
    }

    // === GETTERS AND SETTERS ===

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCompanyCode() { return companyCode; }
    public void setCompanyCode(String companyCode) { this.companyCode = companyCode; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getCompanyShortName() { return companyShortName; }
    public void setCompanyShortName(String companyShortName) { this.companyShortName = companyShortName; }

    public String getRegisteredAddress() { return registeredAddress; }
    public void setRegisteredAddress(String registeredAddress) { this.registeredAddress = registeredAddress; }

    public String getPrimaryEmail() { return primaryEmail; }
    public void setPrimaryEmail(String primaryEmail) { this.primaryEmail = primaryEmail; }

    public Long getRegisteredCountryId() { return registeredCountryId; }
    public void setRegisteredCountryId(Long registeredCountryId) { this.registeredCountryId = registeredCountryId; }

    public CompanyType getCompanyType() { return companyType; }
    public void setCompanyType(CompanyType companyType) { this.companyType = companyType; }

    public Integer getFinancialYearStartMonth() { return financialYearStartMonth; }
    public void setFinancialYearStartMonth(Integer financialYearStartMonth) { this.financialYearStartMonth = financialYearStartMonth; }

    public Integer getFinancialYearEndMonth() { return financialYearEndMonth; }
    public void setFinancialYearEndMonth(Integer financialYearEndMonth) { this.financialYearEndMonth = financialYearEndMonth; }

    public String getDefaultCurrencyCode() { return defaultCurrencyCode; }
    public void setDefaultCurrencyCode(String defaultCurrencyCode) { this.defaultCurrencyCode = defaultCurrencyCode; }

    public String getDefaultTimezone() { return defaultTimezone; }
    public void setDefaultTimezone(String defaultTimezone) { this.defaultTimezone = defaultTimezone; }

    public String getDefaultLanguage() { return defaultLanguage; }
    public void setDefaultLanguage(String defaultLanguage) { this.defaultLanguage = defaultLanguage; }

    public String getDateFormat() { return dateFormat; }
    public void setDateFormat(String dateFormat) { this.dateFormat = dateFormat; }

    public String getTimeFormat() { return timeFormat; }
    public void setTimeFormat(String timeFormat) { this.timeFormat = timeFormat; }

    public Boolean getIsMultiLocation() { return isMultiLocation; }
    public void setIsMultiLocation(Boolean isMultiLocation) { this.isMultiLocation = isMultiLocation; }

    public Boolean getIsMultiCurrency() { return isMultiCurrency; }
    public void setIsMultiCurrency(Boolean isMultiCurrency) { this.isMultiCurrency = isMultiCurrency; }

    public SubscriptionPlan getSubscriptionPlan() { return subscriptionPlan; }
    public void setSubscriptionPlan(SubscriptionPlan subscriptionPlan) { this.subscriptionPlan = subscriptionPlan; }

    public LicenseType getLicenseType() { return licenseType; }
    public void setLicenseType(LicenseType licenseType) { this.licenseType = licenseType; }

    public CompanyStatus getCompanyStatus() { return companyStatus; }
    public void setCompanyStatus(CompanyStatus companyStatus) { this.companyStatus = companyStatus; }

    public Boolean getIsTrial() { return isTrial; }
    public void setIsTrial(Boolean isTrial) { this.isTrial = isTrial; }

    public Boolean getIsDemo() { return isDemo; }
    public void setIsDemo(Boolean isDemo) { this.isDemo = isDemo; }

    public Boolean getEnableBiometricIntegration() { return enableBiometricIntegration; }
    public void setEnableBiometricIntegration(Boolean enableBiometricIntegration) { this.enableBiometricIntegration = enableBiometricIntegration; }

    public Boolean getEnableMobileApp() { return enableMobileApp; }
    public void setEnableMobileApp(Boolean enableMobileApp) { this.enableMobileApp = enableMobileApp; }

    public Boolean getEnableSelfService() { return enableSelfService; }
    public void setEnableSelfService(Boolean enableSelfService) { this.enableSelfService = enableSelfService; }

    public Boolean getEnableWorkflowApprovals() { return enableWorkflowApprovals; }
    public void setEnableWorkflowApprovals(Boolean enableWorkflowApprovals) { this.enableWorkflowApprovals = enableWorkflowApprovals; }

    public Boolean getEnableAuditLogging() { return enableAuditLogging; }
    public void setEnableAuditLogging(Boolean enableAuditLogging) { this.enableAuditLogging = enableAuditLogging; }

    public Boolean getEnableDataEncryption() { return enableDataEncryption; }
    public void setEnableDataEncryption(Boolean enableDataEncryption) { this.enableDataEncryption = enableDataEncryption; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getModifiedAt() { return modifiedAt; }
    public void setModifiedAt(LocalDateTime modifiedAt) { this.modifiedAt = modifiedAt; }

    // === BUSINESS METHODS ===

    public boolean isActive() {
        return companyStatus == CompanyStatus.ACTIVE;
    }

    public void activate() {
        this.companyStatus = CompanyStatus.ACTIVE;
    }

    public void deactivate() {
        this.companyStatus = CompanyStatus.INACTIVE;
    }

    public void suspend() {
        this.companyStatus = CompanyStatus.SUSPENDED;
    }

    public void terminate() {
        this.companyStatus = CompanyStatus.TERMINATED;
    }

    /**
     * Updates company information from another company object.
     * Only updates non-null fields from the source.
     * Does not update audit fields or ID.
     *
     * @param updateData Company object containing fields to update
     */
    public void updateFrom(Company updateData) {
        if (updateData.getCompanyCode() != null) {
            this.companyCode = updateData.getCompanyCode();
        }
        if (updateData.getCompanyName() != null) {
            this.companyName = updateData.getCompanyName();
        }
        if (updateData.getCompanyShortName() != null) {
            this.companyShortName = updateData.getCompanyShortName();
        }
        if (updateData.getRegisteredAddress() != null) {
            this.registeredAddress = updateData.getRegisteredAddress();
        }
        if (updateData.getPrimaryEmail() != null) {
            this.primaryEmail = updateData.getPrimaryEmail();
        }
        if (updateData.getRegisteredCountryId() != null) {
            this.registeredCountryId = updateData.getRegisteredCountryId();
        }
        if (updateData.getCompanyStatus() != null) {
            this.companyStatus = updateData.getCompanyStatus();
        }
        // Add other updateable fields as needed
    }

    // === LEGACY COMPATIBILITY METHODS ===

    /**
     * Legacy getter for backward compatibility with existing code.
     * Maps to companyCode field since registration_number doesn't exist in new schema.
     */
    public String getRegistrationNumber() {
        return companyCode; // Map to company_code for backward compatibility
    }

    /**
     * Legacy setter for backward compatibility with existing code.
     * Maps to companyCode field.
     */
    public void setRegistrationNumber(String registrationNumber) {
        this.companyCode = registrationNumber; // Map to company_code
    }

    /**
     * Legacy getter for backward compatibility with existing code.
     * Returns createdAt timestamp.
     */
    public LocalDateTime getCreatedDate() {
        return createdAt;
    }

    /**
     * Legacy getter for backward compatibility with existing code.
     * Returns modifiedAt timestamp.
     */
    public LocalDateTime getModifiedDate() {
        return modifiedAt;
    }

    /**
     * Legacy constructor for backward compatibility with existing code.
     * Maps old 3-parameter constructor to new 6-parameter constructor.
     * Uses default values for missing required fields.
     */
    public Company(String companyCode, String companyName, String registrationNumber) {
        this(companyCode, companyName, companyName, // Use companyName as shortName
             "Default Address", // Default registered address
             "admin@" + companyCode.toLowerCase() + ".com", // Generate default email
             1L); // Default country ID (assuming India exists as ID 1)
    }

    /**
     * Legacy method for backward compatibility.
     * Disables the company (sets status to INACTIVE).
     */
    public void disable() {
        this.companyStatus = CompanyStatus.INACTIVE;
    }

    /**
     * Legacy method for backward compatibility.
     * Reactivates a disabled company (sets status to ACTIVE).
     */
    public void reactivate() {
        this.companyStatus = CompanyStatus.ACTIVE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return Objects.equals(id, company.id) &&
               Objects.equals(companyCode, company.companyCode) &&
               Objects.equals(primaryEmail, company.primaryEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, companyCode, primaryEmail);
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", companyCode='" + companyCode + '\'' +
                ", companyName='" + companyName + '\'' +
                ", companyShortName='" + companyShortName + '\'' +
                ", primaryEmail='" + primaryEmail + '\'' +
                ", registeredCountryId=" + registeredCountryId +
                ", companyStatus=" + companyStatus +
                ", createdAt=" + createdAt +
                ", modifiedAt=" + modifiedAt +
                '}';
    }
}