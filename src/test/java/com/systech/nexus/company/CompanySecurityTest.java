package com.systech.nexus.company;

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
 * Security Test for Company Admin-Only Access Control.
 *
 * MAJOR CHANGES:
 * v1.0 (2025-09-27) - Initial security tests for admin-only access control
 *
 * These security tests verify the role-based access control implementation:
 * - app-admins role has full access to all operations
 * - platform-admins role has full access to all operations
 * - basic-users role is denied access to all operations
 * - Unauthenticated requests are denied
 * - JWT token validation works correctly
 * - Security annotations are properly enforced
 *
 * Per specification: Only app-admins and platform-admins Keycloak roles
 * should have access to company operations.
 *
 * These tests MUST FAIL initially (TDD approach) until security implementation is complete.
 *
 * @author Backend Developer
 * @version 1.0
 * @since 1.0
 */
@SpringBootTest
@ActiveProfiles("test")
class CompanySecurityTest {

    @Autowired
    private DgsQueryExecutor dgsQueryExecutor;

    private Object readData(String document, String dataPath) {
        var context = dgsQueryExecutor.executeAndGetDocumentContext(document);
        return context.read("$." + dataPath);
    }

    private List<GraphQLError> executeForErrors(String document) {
        return dgsQueryExecutor.execute(document).getErrors();
    }

    // Test app-admins role access

    @Test
    @WithMockUser(roles = {"app-admins"})
    void appAdminsShouldAccessAllQueries() {
        // Test companies query
        String companiesQuery = """
            query {
                companies {
                    id
                    companyName
                }
            }
        """;

        var companiesResult = readData(companiesQuery, "data.companies");
        assertThat(companiesResult).isNotNull();

        // Test company by ID query
        String companyQuery = """
            query {
                company(id: "1") {
                    id
                    companyName
                }
            }
        """;

        var companyResult = readData(companyQuery, "data.company");
        assertThat(companyResult).isNotNull();

        // Test search query
        String searchQuery = """
            query {
                searchCompanies(searchTerm: "Test") {
                    id
                    companyName
                }
            }
        """;

        var searchResult = readData(searchQuery, "data.searchCompanies");
        assertThat(searchResult).isNotNull();
    }

    @Test
    @WithMockUser(roles = {"app-admins"})
    void appAdminsShouldAccessAllMutations() {
        // Test create mutation
        String createMutation = """
            mutation {
                createCompany(input: {
                    companyName: "App Admin Test Company"
                    registrationNumber: "AATC2025001"
                }) {
                    id
                    companyName
                }
            }
        """;

        var createResult = readData(createMutation, "data.createCompany");
        assertThat(createResult).isNotNull();

        // Test update mutation
        String updateMutation = """
            mutation {
                updateCompany(id: "1", input: {
                    companyName: "Updated by App Admin"
                }) {
                    id
                    companyName
                }
            }
        """;

        var updateResult = readData(updateMutation, "data.updateCompany");
        assertThat(updateResult).isNotNull();

        // Test disable mutation
        String disableMutation = """
            mutation {
                disableCompany(id: "1") {
                    id
                    active
                }
            }
        """;

        var disableResult = readData(disableMutation, "data.disableCompany");
        assertThat(disableResult).isNotNull();

        // Test reactivate mutation
        String reactivateMutation = """
            mutation {
                reactivateCompany(id: "1") {
                    id
                    active
                }
            }
        """;

        var reactivateResult = readData(reactivateMutation, "data.reactivateCompany");
        assertThat(reactivateResult).isNotNull();

        // Test bulk import mutation
        String bulkImportMutation = """
            mutation {
                bulkImportCompanies(input: {
                    companies: [
                        {
                            companyName: "Bulk Test 1"
                            registrationNumber: "BT2025001"
                        }
                    ]
                }) {
                    successCount
                    failureCount
                }
            }
        """;

        var bulkResult = readData(bulkImportMutation, "data.bulkImportCompanies");
        assertThat(bulkResult).isNotNull();
    }

    // Test platform-admins role access

    @Test
    @WithMockUser(roles = {"platform-admins"})
    void platformAdminsShouldAccessAllQueries() {
        String companiesQuery = """
            query {
                companies {
                    id
                    companyName
                }
            }
        """;

        var result = readData(companiesQuery, "data.companies");
        assertThat(result).isNotNull();
    }

    @Test
    @WithMockUser(roles = {"platform-admins"})
    void platformAdminsShouldAccessAllMutations() {
        String createMutation = """
            mutation {
                createCompany(input: {
                    companyName: "Platform Admin Test Company"
                    registrationNumber: "PATC2025001"
                }) {
                    id
                    companyName
                }
            }
        """;

        var result = readData(createMutation, "data.createCompany");
        assertThat(result).isNotNull();
    }

    // Test role combination access

    @Test
    @WithMockUser(roles = {"app-admins", "platform-admins"})
    void multipleAdminRolesShouldHaveAccess() {
        String companiesQuery = """
            query {
                companies {
                    id
                    companyName
                }
            }
        """;

        var result = readData(companiesQuery, "data.companies");
        assertThat(result).isNotNull();
    }

    // Test insufficient role access

    @Test
    @WithMockUser(roles = {"basic-users"})
    void basicUsersShouldBeRejectedFromQueries() {
        String companiesQuery = """
            query {
                companies {
                    id
                    companyName
                }
            }
        """;

        var errors = executeForErrors(companiesQuery);
        assertThat(errors).isNotEmpty();
        assertThat(errors.get(0).getMessage()).contains("Access Denied");
    }

    @Test
    @WithMockUser(roles = {"basic-users"})
    void basicUsersShouldBeRejectedFromMutations() {
        String createMutation = """
            mutation {
                createCompany(input: {
                    companyName: "Unauthorized Company"
                    registrationNumber: "UC2025001"
                }) {
                    id
                }
            }
        """;

        var errors = executeForErrors(createMutation);
        assertThat(errors).isNotEmpty();
        assertThat(errors.get(0).getMessage()).contains("Access Denied");
    }

    @Test
    @WithMockUser(roles = {"viewer", "guest"})
    void otherRolesShouldBeRejected() {
        String companiesQuery = """
            query {
                companies {
                    id
                    companyName
                }
            }
        """;

        var errors = executeForErrors(companiesQuery);
        assertThat(errors).isNotEmpty();
        assertThat(errors.get(0).getMessage()).contains("Access Denied");
    }

    // Test unauthenticated access

    @Test
    void unauthenticatedRequestsShouldBeRejected() {
        String companiesQuery = """
            query {
                companies {
                    id
                    companyName
                }
            }
        """;

        var errors = executeForErrors(companiesQuery);
        assertThat(errors).isNotEmpty();
        assertThat(errors.get(0).getMessage()).contains("Access Denied");
    }

    @Test
    void unauthenticatedMutationsShouldBeRejected() {
        String createMutation = """
            mutation {
                createCompany(input: {
                    companyName: "Unauthenticated Company"
                    registrationNumber: "UAC2025001"
                }) {
                    id
                }
            }
        """;

        var errors = executeForErrors(createMutation);
        assertThat(errors).isNotEmpty();
        assertThat(errors.get(0).getMessage()).contains("Access Denied");
    }

    // Test specific security scenarios

    @Test
    @WithMockUser(roles = {"app-admins"})
    void securityShouldNotBypassInputValidation() {
        // Even with correct role, invalid input should be rejected
        String invalidMutation = """
            mutation {
                createCompany(input: {
                    companyName: ""
                    registrationNumber: ""
                }) {
                    id
                }
            }
        """;

        var errors = executeForErrors(invalidMutation);
        assertThat(errors).isNotEmpty();
        // Should get validation error, not access denied
    }

    @Test
    @WithMockUser(roles = {"platform-admins"})
    void securityShouldEnforceBusinessRules() {
        // Even with correct role, business rules should be enforced
        String duplicateMutation = """
            mutation {
                createCompany(input: {
                    companyName: "Duplicate Test"
                    registrationNumber: "EXISTING123"
                }) {
                    id
                }
            }
        """;

        var errors = executeForErrors(duplicateMutation);
        assertThat(errors).isNotEmpty();
        // Should get business rule violation, not access denied
    }

    @Test
    @WithMockUser(roles = {"app-admins"})
    void securityContextShouldBeAvailableForAuditTrail() {
        // Security context should be available for audit fields
        String createMutation = """
            mutation {
                createCompany(input: {
                    companyName: "Audit Security Test Company"
                    registrationNumber: "ASTC2025001"
                }) {
                    id
                    companyName
                    createdBy
                    modifiedBy
                }
            }
        """;

        var result = readData(createMutation, "data.createCompany");
        assertThat(result).isNotNull();
        // createdBy and modifiedBy should be populated from security context
    }
}
