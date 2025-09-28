package com.systech.nexus.company.graphql;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import graphql.GraphQLError;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * GraphQL Contract Test for Company Mutation Operations.
 *
 * MAJOR CHANGES:
 * v1.0 (2025-09-27) - Initial contract tests for companies GraphQL mutations
 *
 * These tests verify the GraphQL schema compliance and mutation functionality:
 * - createCompany mutation with input validation
 * - updateCompany mutation for data modification
 * - disableCompany mutation for soft delete functionality
 * - reactivateCompany mutation for restore functionality
 * - bulkImportCompanies mutation for batch operations
 *
 * All mutations require app-admins or platform-admins authentication.
 * These tests MUST FAIL initially (TDD approach) until implementation is complete.
 *
 * @author Backend Developer
 * @version 1.0
 * @since 1.0
 */
@SpringBootTest
@ActiveProfiles("test")
class CompanyMutationTest {

    @Autowired
    private DgsQueryExecutor dgsQueryExecutor;

    private Object readData(String document, String dataPath) {
        var context = dgsQueryExecutor.executeAndGetDocumentContext(document);
        return context.read("$." + dataPath);
    }

    private List<GraphQLError> executeForErrors(String document) {
        return dgsQueryExecutor.execute(document).getErrors();
    }

    @Test
    @WithMockUser(roles = {"app-admins"})
    void shouldExecuteCreateCompanyMutation() {
        // Given: GraphQL mutation to create company
        String mutation = """
            mutation {
                createCompany(input: {
                    companyName: "Test Company Ltd"
                    registrationNumber: "TC2025001"
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
        """;

        // When: Execute mutation (EXPECTED TO FAIL - no implementation yet)
        // Then: Should return created company
        var result = readData(mutation, "data.createCompany");

        // Verify response structure (will fail until CompanyDataFetcher is implemented)
        assertThat(result).isNotNull();
    }

    @Test
    @WithMockUser(roles = {"platform-admins"})
    void shouldExecuteUpdateCompanyMutation() {
        // Given: GraphQL mutation to update company
        String mutation = """
            mutation {
                updateCompany(id: "1", input: {
                    companyName: "Updated Company Name Ltd"
                    registrationNumber: "UC2025001"
                }) {
                    id
                    companyName
                    registrationNumber
                    modifiedDate
                    modifiedBy
                }
            }
        """;

        // When: Execute mutation (EXPECTED TO FAIL - no implementation yet)
        // Then: Should return updated company
        var result = readData(mutation, "data.updateCompany");

        // Verify response structure (will fail until CompanyDataFetcher is implemented)
        assertThat(result).isNotNull();
    }

    @Test
    @WithMockUser(roles = {"app-admins"})
    void shouldExecuteDisableCompanyMutation() {
        // Given: GraphQL mutation to disable company (soft delete)
        String mutation = """
            mutation {
                disableCompany(id: "1") {
                    id
                    companyName
                    active
                    modifiedDate
                    modifiedBy
                }
            }
        """;

        // When: Execute mutation (EXPECTED TO FAIL - no implementation yet)
        // Then: Should return disabled company with active=false
        var result = readData(mutation, "data.disableCompany");

        // Verify response structure (will fail until CompanyDataFetcher is implemented)
        assertThat(result).isNotNull();
    }

    @Test
    @WithMockUser(roles = {"platform-admins"})
    void shouldExecuteReactivateCompanyMutation() {
        // Given: GraphQL mutation to reactivate company
        String mutation = """
            mutation {
                reactivateCompany(id: "1") {
                    id
                    companyName
                    active
                    modifiedDate
                    modifiedBy
                }
            }
        """;

        // When: Execute mutation (EXPECTED TO FAIL - no implementation yet)
        // Then: Should return reactivated company with active=true
        var result = readData(mutation, "data.reactivateCompany");

        // Verify response structure (will fail until CompanyDataFetcher is implemented)
        assertThat(result).isNotNull();
    }

    @Test
    @WithMockUser(roles = {"platform-admins"})
    void shouldExecuteBulkImportCompaniesMutation() {
        // Given: GraphQL mutation for bulk import
        String mutation = """
            mutation {
                bulkImportCompanies(input: {
                    companies: [
                        {
                            companyName: "Bulk Company 1"
                            registrationNumber: "BC2025001"
                        },
                        {
                            companyName: "Bulk Company 2"
                            registrationNumber: "BC2025002"
                        }
                    ]
                }) {
                    successCount
                    failureCount
                    errors
                }
            }
        """;

        // When: Execute mutation (EXPECTED TO FAIL - no implementation yet)
        // Then: Should return import results
        var result = readData(mutation, "data.bulkImportCompanies");

        // Verify response structure (will fail until CompanyDataFetcher is implemented)
        assertThat(result).isNotNull();
    }

    @Test
    @WithMockUser(roles = {"app-admins"})
    void shouldValidateCreateCompanyInputRequired() {
        // Given: GraphQL mutation with missing required fields
        String mutation = """
            mutation {
                createCompany(input: {
                    companyName: ""
                    registrationNumber: ""
                }) {
                    id
                }
            }
        """;

        // When: Execute mutation with invalid input
        // Then: Should return validation errors
        var errors = executeForErrors(mutation);

        // Verify input validation (will fail until validation is implemented)
        assertThat(errors).isNotEmpty();
    }

    @Test
    @WithMockUser(roles = {"app-admins"})
    void shouldValidateUniqueRegistrationNumber() {
        // Given: GraphQL mutation with duplicate registration number
        String mutation = """
            mutation {
                createCompany(input: {
                    companyName: "Duplicate Test Company"
                    registrationNumber: "EXISTING123"
                }) {
                    id
                }
            }
        """;

        // When: Execute mutation with duplicate registration number
        // Then: Should return uniqueness constraint error
        var errors = executeForErrors(mutation);

        // Verify uniqueness validation (will fail until constraint is implemented)
        assertThat(errors).isNotEmpty();
    }

    @Test
    void shouldRejectUnauthorizedCreateCompany() {
        // Given: GraphQL mutation without authentication
        String mutation = """
            mutation {
                createCompany(input: {
                    companyName: "Unauthorized Company"
                    registrationNumber: "UC2025001"
                }) {
                    id
                }
            }
        """;

        // When: Execute mutation without authentication
        // Then: Should be rejected with authentication error
        var errors = executeForErrors(mutation);

        // Verify security restrictions are enforced
        assertThat(errors).isNotEmpty();
        assertThat(errors.get(0).getMessage()).contains("Access Denied");
    }

    @Test
    @WithMockUser(roles = {"basic-users"})
    void shouldRejectInsufficientRoleForMutations() {
        // Given: GraphQL mutation with insufficient role
        String mutation = """
            mutation {
                createCompany(input: {
                    companyName: "Insufficient Role Company"
                    registrationNumber: "IRC2025001"
                }) {
                    id
                }
            }
        """;

        // When: Execute mutation with basic-users role (insufficient)
        // Then: Should be rejected with authorization error
        var errors = executeForErrors(mutation);

        // Verify role-based access control
        assertThat(errors).isNotEmpty();
        assertThat(errors.get(0).getMessage()).contains("Access Denied");
    }
}
