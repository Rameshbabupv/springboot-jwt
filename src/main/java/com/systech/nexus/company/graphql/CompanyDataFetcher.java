package com.systech.nexus.company.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import com.systech.nexus.common.annotation.Loggable;
import com.systech.nexus.company.domain.Company;
import com.systech.nexus.company.service.CompanyService;
import com.systech.nexus.country.repository.CountryRepository;
import graphql.execution.DataFetcherResult;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * GraphQL Data Fetcher for Company entity operations with JWT-based security.
 *
 * MAJOR CHANGES:
 * v1.0 (2025-09-27) - Initial implementation with complete CRUD operations and security
 *
 * Features:
 * - Complete CRUD operations via GraphQL with JWT authentication
 * - Admin-only access control (app-admins and platform-admins)
 * - Comprehensive error handling with detailed GraphQL errors
 * - Input validation and sanitization
 * - Soft delete operations (disable/reactivate)
 * - Bulk import functionality with detailed results
 * - Advanced search and filtering capabilities
 * - Custom data fetchers for timestamp formatting
 * - AOP logging integration for audit trails
 *
 * Security Model:
 * - All operations: Requires admin roles only (app-admins or platform-admins)
 * - No read-only access for basic users per specification
 * - All company operations are administrative functions
 *
 * API Contract:
 * - Queries: companies(search), company(id), searchCompanies(searchTerm)
 * - Mutations: create, update, disable, reactivate, bulkImport
 * - Input/Output: Follows GraphQL schema contracts from tests
 * - Error Handling: Returns detailed errors in GraphQL format
 *
 * All operations return DataFetcherResult to handle both success and error cases
 * gracefully in the GraphQL response format.
 *
 * @author Backend Developer
 * @version 1.0
 * @since 1.0
 */
@DgsComponent
public class CompanyDataFetcher {

    private final CompanyService companyService;
    private final CountryRepository countryRepository;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Autowired
    public CompanyDataFetcher(CompanyService companyService, CountryRepository countryRepository) {
        this.companyService = companyService;
        this.countryRepository = countryRepository;
    }

    // QUERIES (require admin privileges only)

    /**
     * Get all companies with optional search criteria.
     * Supports filtering by company name, registration number, and active status.
     *
     * @param search optional search criteria map
     * @return list of companies matching the criteria
     */
    @DgsQuery
    @PreAuthorize("hasAnyRole('app-admins', 'platform-admins')")
    @Loggable(description = "GraphQL query: get companies with search")
    public DataFetcherResult<List<Company>> companies(@InputArgument Map<String, Object> search) {
        try {
            List<Company> companies;

            if (search == null || search.isEmpty()) {
                // Return all companies if no search criteria
                companies = companyService.getAllCompanies();
            } else {
                // Extract search parameters
                String companyName = (String) search.get("companyName");
                String registrationNumber = (String) search.get("registrationNumber");
                Boolean active = (Boolean) search.get("active");

                // Use advanced search with criteria
                companies = companyService.searchCompaniesByCriteria(companyName, registrationNumber, active);
            }

            return DataFetcherResult.<List<Company>>newResult()
                .data(companies)
                .build();

        } catch (Exception e) {
            GraphQLError error = GraphqlErrorBuilder.newError()
                .message("Failed to fetch companies: " + e.getMessage())
                .build();
            return DataFetcherResult.<List<Company>>newResult()
                .error(error)
                .build();
        }
    }

    /**
     * Get a specific company by ID.
     *
     * @param id the company ID to retrieve
     * @return the company if found, null otherwise
     */
    @DgsQuery
    @PreAuthorize("hasAnyRole('app-admins', 'platform-admins')")
    @Loggable(description = "GraphQL query: get company by ID")
    public DataFetcherResult<Company> company(@InputArgument String id) {
        try {
            Long companyId = Long.parseLong(id);
            Optional<Company> company = companyService.getCompanyById(companyId);

            if (company.isPresent()) {
                return DataFetcherResult.<Company>newResult()
                    .data(company.get())
                    .build();
            } else {
                // Return null for non-existent company (not an error per GraphQL standards)
                return DataFetcherResult.<Company>newResult()
                    .data(null)
                    .build();
            }

        } catch (NumberFormatException e) {
            GraphQLError error = GraphqlErrorBuilder.newError()
                .message("Invalid company ID format: " + id)
                .build();
            return DataFetcherResult.<Company>newResult()
                .error(error)
                .build();
        } catch (Exception e) {
            GraphQLError error = GraphqlErrorBuilder.newError()
                .message("Failed to fetch company: " + e.getMessage())
                .build();
            return DataFetcherResult.<Company>newResult()
                .error(error)
                .build();
        }
    }

    /**
     * Search companies by text across company name and registration number.
     *
     * @param searchTerm the text to search for
     * @return list of companies matching the search term
     */
    @DgsQuery
    @PreAuthorize("hasAnyRole('app-admins', 'platform-admins')")
    @Loggable(description = "GraphQL query: search companies by text")
    public DataFetcherResult<List<Company>> searchCompanies(@InputArgument String searchTerm) {
        try {
            List<Company> companies = companyService.searchCompanies(searchTerm);
            return DataFetcherResult.<List<Company>>newResult()
                .data(companies)
                .build();

        } catch (Exception e) {
            GraphQLError error = GraphqlErrorBuilder.newError()
                .message("Failed to search companies: " + e.getMessage())
                .build();
            return DataFetcherResult.<List<Company>>newResult()
                .error(error)
                .build();
        }
    }

    // MUTATIONS (require admin privileges only)

    /**
     * Create a new company.
     *
     * @param input company creation data
     * @return the created company
     */
    @DgsMutation
    @PreAuthorize("hasAnyRole('app-admins', 'platform-admins')")
    @Loggable(description = "GraphQL mutation: create company")
    public DataFetcherResult<Company> createCompany(@InputArgument Map<String, Object> input) {
        try {
            String companyName = (String) input.get("companyName");
            String registrationNumber = (String) input.get("registrationNumber");
            Boolean active = (Boolean) input.get("active");

            // Basic validation
            if (companyName == null || companyName.trim().isEmpty()) {
                GraphQLError error = GraphqlErrorBuilder.newError()
                    .message("Company name is required")
                    .build();
                return DataFetcherResult.<Company>newResult()
                    .error(error)
                    .build();
            }

            if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
                GraphQLError error = GraphqlErrorBuilder.newError()
                    .message("Registration number is required")
                    .build();
                return DataFetcherResult.<Company>newResult()
                    .error(error)
                    .build();
            }

            Company newCompany = companyService.createCompany(companyName, registrationNumber, active);
            return DataFetcherResult.<Company>newResult()
                .data(newCompany)
                .build();

        } catch (Exception e) {
            GraphQLError error = GraphqlErrorBuilder.newError()
                .message("Failed to create company: " + e.getMessage())
                .build();
            return DataFetcherResult.<Company>newResult()
                .error(error)
                .build();
        }
    }

    /**
     * Update an existing company.
     *
     * @param id    the company ID to update
     * @param input company update data
     * @return the updated company
     */
    @DgsMutation
    @PreAuthorize("hasAnyRole('app-admins', 'platform-admins')")
    @Loggable(description = "GraphQL mutation: update company")
    public DataFetcherResult<Company> updateCompany(@InputArgument String id, @InputArgument Map<String, Object> input) {
        try {
            Long companyId = Long.parseLong(id);
            String companyName = (String) input.get("companyName");
            String registrationNumber = (String) input.get("registrationNumber");

            Company updatedCompany = companyService.updateCompany(companyId, companyName, registrationNumber);
            return DataFetcherResult.<Company>newResult()
                .data(updatedCompany)
                .build();

        } catch (NumberFormatException e) {
            GraphQLError error = GraphqlErrorBuilder.newError()
                .message("Invalid company ID format: " + id)
                .build();
            return DataFetcherResult.<Company>newResult()
                .error(error)
                .build();
        } catch (Exception e) {
            GraphQLError error = GraphqlErrorBuilder.newError()
                .message("Failed to update company: " + e.getMessage())
                .build();
            return DataFetcherResult.<Company>newResult()
                .error(error)
                .build();
        }
    }

    /**
     * Disable a company (soft delete).
     *
     * @param id the company ID to disable
     * @return the disabled company
     */
    @DgsMutation
    @PreAuthorize("hasAnyRole('app-admins', 'platform-admins')")
    @Loggable(description = "GraphQL mutation: disable company")
    public DataFetcherResult<Company> disableCompany(@InputArgument String id) {
        try {
            Long companyId = Long.parseLong(id);
            Company disabledCompany = companyService.disableCompany(companyId);
            return DataFetcherResult.<Company>newResult()
                .data(disabledCompany)
                .build();

        } catch (NumberFormatException e) {
            GraphQLError error = GraphqlErrorBuilder.newError()
                .message("Invalid company ID format: " + id)
                .build();
            return DataFetcherResult.<Company>newResult()
                .error(error)
                .build();
        } catch (Exception e) {
            GraphQLError error = GraphqlErrorBuilder.newError()
                .message("Failed to disable company: " + e.getMessage())
                .build();
            return DataFetcherResult.<Company>newResult()
                .error(error)
                .build();
        }
    }

    /**
     * Reactivate a disabled company.
     *
     * @param id the company ID to reactivate
     * @return the reactivated company
     */
    @DgsMutation
    @PreAuthorize("hasAnyRole('app-admins', 'platform-admins')")
    @Loggable(description = "GraphQL mutation: reactivate company")
    public DataFetcherResult<Company> reactivateCompany(@InputArgument String id) {
        try {
            Long companyId = Long.parseLong(id);
            Company reactivatedCompany = companyService.reactivateCompany(companyId);
            return DataFetcherResult.<Company>newResult()
                .data(reactivatedCompany)
                .build();

        } catch (NumberFormatException e) {
            GraphQLError error = GraphqlErrorBuilder.newError()
                .message("Invalid company ID format: " + id)
                .build();
            return DataFetcherResult.<Company>newResult()
                .error(error)
                .build();
        } catch (Exception e) {
            GraphQLError error = GraphqlErrorBuilder.newError()
                .message("Failed to reactivate company: " + e.getMessage())
                .build();
            return DataFetcherResult.<Company>newResult()
                .error(error)
                .build();
        }
    }

    /**
     * Bulk import companies from a list.
     *
     * @param input bulk import data containing list of companies
     * @return import results with success/failure counts and errors
     */
    @DgsMutation
    @PreAuthorize("hasAnyRole('app-admins', 'platform-admins')")
    @Loggable(description = "GraphQL mutation: bulk import companies")
    @SuppressWarnings("unchecked")
    public DataFetcherResult<BulkImportResult> bulkImportCompanies(@InputArgument Map<String, Object> input) {
        try {
            List<Map<String, Object>> companiesData = (List<Map<String, Object>>) input.get("companies");

            if (companiesData == null || companiesData.isEmpty()) {
                GraphQLError error = GraphqlErrorBuilder.newError()
                    .message("Companies list is required and cannot be empty")
                    .build();
                return DataFetcherResult.<BulkImportResult>newResult()
                    .error(error)
                    .build();
            }

            // Convert to service import data objects
            List<CompanyService.CompanyImportData> importDataList = new ArrayList<>();
            for (Map<String, Object> companyData : companiesData) {
                String companyName = (String) companyData.get("companyName");
                String registrationNumber = (String) companyData.get("registrationNumber");
                Boolean active = (Boolean) companyData.get("active");

                CompanyService.CompanyImportData importData = new CompanyService.CompanyImportData(
                    companyName, registrationNumber, active
                );
                importDataList.add(importData);
            }

            // Perform bulk import
            CompanyService.BulkImportResult serviceResult = companyService.bulkImportCompanies(importDataList);

            // Convert to GraphQL result
            BulkImportResult result = new BulkImportResult(
                serviceResult.getSuccessCount(),
                serviceResult.getFailureCount(),
                serviceResult.getErrors()
            );

            return DataFetcherResult.<BulkImportResult>newResult()
                .data(result)
                .build();

        } catch (Exception e) {
            GraphQLError error = GraphqlErrorBuilder.newError()
                .message("Failed to bulk import companies: " + e.getMessage())
                .build();
            return DataFetcherResult.<BulkImportResult>newResult()
                .error(error)
                .build();
        }
    }

    // DATA FETCHERS FOR CUSTOM FIELDS

    /**
     * Format createdDate as ISO string for GraphQL response.
     *
     * @param dfe data fetching environment containing the Company source
     * @return formatted creation date string
     */
    @DgsData(parentType = "Company", field = "createdDate")
    public String createdDate(DataFetchingEnvironment dfe) {
        Company company = dfe.getSource();
        return company.getCreatedDate() != null ? company.getCreatedDate().format(dateTimeFormatter) : null;
    }

    /**
     * Format modifiedDate as ISO string for GraphQL response.
     *
     * @param dfe data fetching environment containing the Company source
     * @return formatted modification date string
     */
    @DgsData(parentType = "Company", field = "modifiedDate")
    public String modifiedDate(DataFetchingEnvironment dfe) {
        Company company = dfe.getSource();
        return company.getModifiedDate() != null ? company.getModifiedDate().format(dateTimeFormatter) : null;
    }

    /**
     * Format createdAt as ISO string for GraphQL response.
     *
     * @param dfe data fetching environment containing the Company source
     * @return formatted creation timestamp string
     */
    @DgsData(parentType = "Company", field = "createdAt")
    public String createdAt(DataFetchingEnvironment dfe) {
        Company company = dfe.getSource();
        return company.getCreatedAt() != null ? company.getCreatedAt().format(dateTimeFormatter) : null;
    }

    /**
     * Format modifiedAt as ISO string for GraphQL response.
     *
     * @param dfe data fetching environment containing the Company source
     * @return formatted modification timestamp string
     */
    @DgsData(parentType = "Company", field = "modifiedAt")
    public String modifiedAt(DataFetchingEnvironment dfe) {
        Company company = dfe.getSource();
        return company.getModifiedAt() != null ? company.getModifiedAt().format(dateTimeFormatter) : null;
    }

    /**
     * Resolve country name from registeredCountryId for GraphQL response.
     *
     * @param dfe data fetching environment containing the Company source
     * @return country name if found, "Unknown" otherwise
     */
    @DgsData(parentType = "Company", field = "country")
    public String country(DataFetchingEnvironment dfe) {
        Company company = dfe.getSource();
        if (company.getRegisteredCountryId() != null) {
            String countryName = countryRepository.findCountryNameById(company.getRegisteredCountryId());
            return countryName != null ? countryName : "Unknown";
        }
        return "Unknown";
    }

    /**
     * GraphQL result type for bulk import operations.
     */
    public static class BulkImportResult {
        private final int successCount;
        private final int failureCount;
        private final List<String> errors;

        public BulkImportResult(int successCount, int failureCount, List<String> errors) {
            this.successCount = successCount;
            this.failureCount = failureCount;
            this.errors = errors != null ? errors : new ArrayList<>();
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
    }
}