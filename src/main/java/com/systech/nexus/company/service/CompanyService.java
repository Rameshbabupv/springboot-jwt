package com.systech.nexus.company.service;

import com.systech.nexus.common.annotation.Loggable;
import com.systech.nexus.company.domain.Company;
import com.systech.nexus.company.repository.CompanyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for Company entity business logic and operations.
 *
 * MAJOR CHANGES:
 * v1.0 (2025-09-27) - Initial CRUD operations with transaction management and validation
 *
 * Responsibilities:
 * - Transactional CRUD operations for data consistency
 * - Business rule validation (uniqueness, required fields)
 * - Soft delete management (disable/reactivate operations)
 * - Bulk import functionality with error handling
 * - Search and filtering operations
 * - AOP logging integration for audit trails
 *
 * Transaction Boundaries:
 * - All write operations are transactional
 * - Read operations use readOnly transactions for performance
 * - Bulk operations are atomic (all succeed or all fail)
 *
 * Error Handling Strategy:
 * - EntityNotFoundException for missing entities
 * - DataIntegrityViolationException for constraint violations
 * - IllegalArgumentException for invalid business rules
 * - Detailed error messages for client feedback
 *
 * Dependencies:
 * - CompanyRepository for data access
 * - Spring Security context for audit trail (via JPA auditing)
 * - @Loggable annotation for automatic operation logging
 *
 * @author Backend Developer
 * @version 1.0
 * @since 1.0
 */
@Service
@Loggable(description = "Company Service Operations")
public class CompanyService {

    private final CompanyRepository companyRepository;

    @Autowired
    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    /**
     * Retrieve all companies ordered by creation date (newest first).
     *
     * @return list of all companies (including inactive)
     */
    @Transactional(readOnly = true)
    @Loggable(description = "Get all companies")
    public List<Company> getAllCompanies() {
        return companyRepository.findAllOrderByCreatedDateDesc();
    }

    /**
     * Retrieve only active companies ordered by name.
     * Used for standard company listings.
     *
     * @return list of active companies only
     */
    @Transactional(readOnly = true)
    @Loggable(description = "Get active companies")
    public List<Company> getActiveCompanies() {
        return companyRepository.findAllActiveOrderByName();
    }

    /**
     * Find company by ID.
     * Returns both active and inactive companies.
     *
     * @param id the company ID to search for
     * @return Optional containing the company if found
     */
    @Transactional(readOnly = true)
    @Loggable(description = "Get company by ID")
    public Optional<Company> getCompanyById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
        return companyRepository.findById(id);
    }

    /**
     * Find company by registration number.
     * Returns both active and inactive companies.
     *
     * @param registrationNumber the registration number to search for
     * @return Optional containing the company if found
     */
    @Transactional(readOnly = true)
    @Loggable(description = "Get company by registration number")
    public Optional<Company> getCompanyByRegistrationNumber(String registrationNumber) {
        if (!StringUtils.hasText(registrationNumber)) {
            throw new IllegalArgumentException("Registration number cannot be null or empty");
        }
        return companyRepository.findByCompanyCode(registrationNumber);
    }

    /**
     * Create a new company with validation.
     *
     * @param companyName        the name of the company (required)
     * @param registrationNumber the unique registration number (required)
     * @param active             the active status (defaults to true if null)
     * @return the created company
     * @throws DataIntegrityViolationException if registration number already exists
     * @throws IllegalArgumentException if required fields are missing
     */
    @Transactional
    @Loggable(description = "Create new company")
    public Company createCompany(String companyName, String registrationNumber, Boolean active) {
        // Validate required fields
        if (!StringUtils.hasText(companyName)) {
            throw new IllegalArgumentException("Company name is required");
        }
        if (!StringUtils.hasText(registrationNumber)) {
            throw new IllegalArgumentException("Registration number is required");
        }

        // Validate uniqueness
        if (companyRepository.existsByCompanyCode(registrationNumber)) {
            throw new DataIntegrityViolationException("Registration number '" + registrationNumber + "' already exists");
        }

        // Generate company code from registration number if not provided
        String companyCode = generateCompanyCode(registrationNumber.trim());
        Company newCompany = new Company(companyCode, companyName.trim(), registrationNumber.trim());
        if (active != null && !active) {
            newCompany.setCompanyStatus(Company.CompanyStatus.INACTIVE);
        }
        return companyRepository.save(newCompany);
    }

    /**
     * Update an existing company with partial update support.
     * Only updates fields that are provided (non-null).
     *
     * @param id                 the ID of the company to update
     * @param companyName        the new company name (optional)
     * @param registrationNumber the new registration number (optional)
     * @return the updated company
     * @throws EntityNotFoundException if company not found
     * @throws DataIntegrityViolationException if new registration number already exists
     */
    @Transactional
    @Loggable(description = "Update existing company")
    public Company updateCompany(Long id, String companyName, String registrationNumber) {
        if (id == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }

        Company existingCompany = companyRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + id));

        // Check for registration number conflicts only if value is being changed
        if (StringUtils.hasText(registrationNumber) &&
            !registrationNumber.equals(existingCompany.getRegistrationNumber())) {
            if (companyRepository.existsByCompanyCode(registrationNumber)) {
                throw new DataIntegrityViolationException("Registration number '" + registrationNumber + "' already exists");
            }
        }

        // Create update object with only non-null values
        Company updateData = new Company();
        if (StringUtils.hasText(companyName)) {
            updateData.setCompanyName(companyName.trim());
        }
        if (StringUtils.hasText(registrationNumber)) {
            updateData.setRegistrationNumber(registrationNumber.trim());
        }

        // Apply partial update
        existingCompany.updateFrom(updateData);

        return companyRepository.save(existingCompany);
    }

    /**
     * Disable a company (soft delete).
     * Sets active status to false while preserving the record.
     *
     * @param id the ID of the company to disable
     * @return the disabled company
     * @throws EntityNotFoundException if company not found
     */
    @Transactional
    @Loggable(description = "Disable company")
    public Company disableCompany(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }

        Company company = companyRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + id));

        company.disable();
        return companyRepository.save(company);
    }

    /**
     * Reactivate a disabled company.
     * Sets active status to true.
     *
     * @param id the ID of the company to reactivate
     * @return the reactivated company
     * @throws EntityNotFoundException if company not found
     */
    @Transactional
    @Loggable(description = "Reactivate company")
    public Company reactivateCompany(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }

        Company company = companyRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + id));

        company.reactivate();
        return companyRepository.save(company);
    }

    /**
     * Search companies by text across company name and registration number.
     * Returns both active and inactive companies matching the search term.
     *
     * @param searchTerm the text to search for (case-insensitive)
     * @return list of companies matching the search term
     */
    @Transactional(readOnly = true)
    @Loggable(description = "Search companies by text")
    public List<Company> searchCompanies(String searchTerm) {
        if (!StringUtils.hasText(searchTerm)) {
            return getAllCompanies();
        }
        return companyRepository.searchByText(searchTerm.trim());
    }

    /**
     * Search active companies by text.
     * Returns only active companies matching the search term.
     *
     * @param searchTerm the text to search for (case-insensitive)
     * @return list of active companies matching the search term
     */
    @Transactional(readOnly = true)
    @Loggable(description = "Search active companies by text")
    public List<Company> searchActiveCompanies(String searchTerm) {
        if (!StringUtils.hasText(searchTerm)) {
            return getActiveCompanies();
        }
        return companyRepository.searchByTextAndStatus(searchTerm.trim(), Company.CompanyStatus.ACTIVE);
    }

    /**
     * Advanced search with multiple optional criteria.
     * All parameters are optional - null values are ignored.
     *
     * @param companyName        optional company name search term
     * @param registrationNumber optional registration number search term
     * @param active             optional active status filter
     * @return list of companies matching all provided criteria
     */
    @Transactional(readOnly = true)
    @Loggable(description = "Search companies with criteria")
    public List<Company> searchCompaniesByCriteria(String companyName, String registrationNumber, Boolean active) {
        Company.CompanyStatus status = null;
        if (active != null) {
            status = active ? Company.CompanyStatus.ACTIVE : Company.CompanyStatus.INACTIVE;
        }
        return companyRepository.findBySearchCriteria(
            StringUtils.hasText(companyName) ? companyName.trim() : null,
            StringUtils.hasText(registrationNumber) ? registrationNumber.trim() : null,
            status
        );
    }

    /**
     * Bulk import companies from a list of company data.
     * All imports are processed in a single transaction (atomic operation).
     *
     * @param companyDataList list of company data to import
     * @return BulkImportResult containing success/failure counts and error details
     */
    @Transactional
    @Loggable(description = "Bulk import companies")
    public BulkImportResult bulkImportCompanies(List<CompanyImportData> companyDataList) {
        if (companyDataList == null || companyDataList.isEmpty()) {
            throw new IllegalArgumentException("Company data list cannot be null or empty");
        }

        List<String> errors = new ArrayList<>();
        List<Company> successfulImports = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        for (int i = 0; i < companyDataList.size(); i++) {
            CompanyImportData data = companyDataList.get(i);
            try {
                // Validate individual company data
                if (!StringUtils.hasText(data.getCompanyName())) {
                    errors.add("Row " + (i + 1) + ": Company name is required");
                    failureCount++;
                    continue;
                }
                if (!StringUtils.hasText(data.getRegistrationNumber())) {
                    errors.add("Row " + (i + 1) + ": Registration number is required");
                    failureCount++;
                    continue;
                }

                // Check for duplicates within the batch
                boolean duplicateInBatch = false;
                for (int j = 0; j < i; j++) {
                    if (data.getRegistrationNumber().equals(companyDataList.get(j).getRegistrationNumber())) {
                        errors.add("Row " + (i + 1) + ": Duplicate registration number '" +
                                 data.getRegistrationNumber() + "' in batch");
                        duplicateInBatch = true;
                        break;
                    }
                }
                if (duplicateInBatch) {
                    failureCount++;
                    continue;
                }

                // Check for existing registration number in database
                if (companyRepository.existsByCompanyCode(data.getRegistrationNumber())) {
                    errors.add("Row " + (i + 1) + ": Registration number '" +
                             data.getRegistrationNumber() + "' already exists");
                    failureCount++;
                    continue;
                }

                // Create and save company
                String companyCode = generateCompanyCode(data.getRegistrationNumber().trim());
                Company company = new Company(
                    companyCode,
                    data.getCompanyName().trim(),
                    data.getRegistrationNumber().trim()
                );
                if (data.getActive() != null && !data.getActive()) {
                    company.setCompanyStatus(Company.CompanyStatus.INACTIVE);
                }
                Company savedCompany = companyRepository.save(company);
                successfulImports.add(savedCompany);
                successCount++;

            } catch (Exception e) {
                errors.add("Row " + (i + 1) + ": " + e.getMessage());
                failureCount++;
            }
        }

        return new BulkImportResult(successCount, failureCount, errors, successfulImports);
    }

    /**
     * Check if a registration number already exists.
     *
     * @param registrationNumber the registration number to check
     * @return true if registration number exists, false otherwise
     */
    @Transactional(readOnly = true)
    @Loggable(description = "Check if registration number exists")
    public boolean registrationNumberExists(String registrationNumber) {
        if (!StringUtils.hasText(registrationNumber)) {
            return false;
        }
        return companyRepository.existsByCompanyCode(registrationNumber);
    }

    /**
     * Get total count of companies.
     *
     * @return total number of companies (including inactive)
     */
    @Transactional(readOnly = true)
    @Loggable(description = "Get company count")
    public long getCompanyCount() {
        return companyRepository.count();
    }

    /**
     * Get count of active companies.
     *
     * @return number of active companies only
     */
    @Transactional(readOnly = true)
    @Loggable(description = "Get active company count")
    public long getActiveCompanyCount() {
        return companyRepository.findByCompanyStatus(Company.CompanyStatus.ACTIVE).size();
    }

    /**
     * Data transfer object for company import operations.
     */
    public static class CompanyImportData {
        private String companyName;
        private String registrationNumber;
        private Boolean active;

        public CompanyImportData() {}

        public CompanyImportData(String companyName, String registrationNumber, Boolean active) {
            this.companyName = companyName;
            this.registrationNumber = registrationNumber;
            this.active = active;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getRegistrationNumber() {
            return registrationNumber;
        }

        public void setRegistrationNumber(String registrationNumber) {
            this.registrationNumber = registrationNumber;
        }

        public Boolean getActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }
    }

    /**
     * Result object for bulk import operations.
     */
    public static class BulkImportResult {
        private final int successCount;
        private final int failureCount;
        private final List<String> errors;
        private final List<Company> successfulImports;

        public BulkImportResult(int successCount, int failureCount, List<String> errors, List<Company> successfulImports) {
            this.successCount = successCount;
            this.failureCount = failureCount;
            this.errors = errors;
            this.successfulImports = successfulImports;
        }

        public int getSuccessCount() {
            return successCount;
        }

        public int getFailureCount() {
            return failureCount;
        }

        public List<String> getErrors() {
            return errors;
        }

        public List<Company> getSuccessfulImports() {
            return successfulImports;
        }
    }

    /**
     * Generates a unique company code from the registration number.
     * Takes the first 8 characters of the registration number and ensures uniqueness.
     *
     * @param registrationNumber the registration number to base the code on
     * @return a unique company code
     */
    private String generateCompanyCode(String registrationNumber) {
        // Take first 8 characters of registration number, removing special characters
        String baseCode = registrationNumber.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        if (baseCode.length() > 8) {
            baseCode = baseCode.substring(0, 8);
        } else if (baseCode.length() < 3) {
            baseCode = baseCode + "000";
        }

        // Ensure uniqueness by checking database and adding suffix if needed
        String companyCode = baseCode;
        int counter = 1;
        while (companyRepository.existsByCompanyCode(companyCode)) {
            companyCode = baseCode + String.format("%02d", counter);
            counter++;
            if (counter > 99) {
                throw new IllegalStateException("Unable to generate unique company code for registration: " + registrationNumber);
            }
        }

        return companyCode;
    }
}