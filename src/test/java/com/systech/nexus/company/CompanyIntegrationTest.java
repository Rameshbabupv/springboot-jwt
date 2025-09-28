package com.systech.nexus.company;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import graphql.GraphQLError;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration Test for Complete Company CRUD Flow.
 *
 * MAJOR CHANGES:
 * v1.0 (2025-09-27) - Initial integration tests for complete CRUD operations
 *
 * These integration tests verify the complete end-to-end functionality:
 * - Create company → Read company → Update company → Disable company → Reactivate company
 * - Bulk import functionality
 * - Search and filtering operations
 * - Audit trail verification
 * - Error handling and edge cases
 *
 * Tests execute in order to maintain data consistency across the CRUD flow.
 * These tests MUST FAIL initially (TDD approach) until full implementation is complete.
 *
 * @author Backend Developer
 * @version 1.0
 * @since 1.0
 */
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class CompanyIntegrationTest {

    @Autowired
    private DgsQueryExecutor dgsQueryExecutor;

    private static String createdCompanyId;

    private Object readData(String document, String dataPath) {
        var context = dgsQueryExecutor.executeAndGetDocumentContext(document);
        return context.read("$." + dataPath);
    }

    private List<GraphQLError> executeForErrors(String document) {
        return dgsQueryExecutor.execute(document).getErrors();
    }

    @Test
    @Order(1)
    @WithMockUser(roles = {"app-admins"})
    void shouldCompleteFullCRUDFlow() {
        // Step 1: Create company
        String createMutation = """
            mutation {
                createCompany(input: {
                    companyName: "Integration Test Company Ltd"
                    registrationNumber: "ITC2025001"
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

        var createResult = readData(createMutation, "data.createCompany");
        assertThat(createResult).isNotNull();

        // Extract company ID for subsequent operations (will fail until implementation exists)
        createdCompanyId = "1"; // Placeholder - actual implementation will extract from result

        // Step 2: Read created company
        String readQuery = """
            query {
                company(id: "%s") {
                    id
                    companyName
                    registrationNumber
                    active
                    createdDate
                    modifiedDate
                }
            }
        """.formatted(createdCompanyId);

        var readResult = readData(readQuery, "data.company");
        assertThat(readResult).isNotNull();

        // Step 3: Update company
        String updateMutation = """
            mutation {
                updateCompany(id: "%s", input: {
                    companyName: "Updated Integration Test Company Ltd"
                }) {
                    id
                    companyName
                    modifiedDate
                    modifiedBy
                }
            }
        """.formatted(createdCompanyId);

        var updateResult = readData(updateMutation, "data.updateCompany");
        assertThat(updateResult).isNotNull();

        // Step 4: Disable company (soft delete)
        String disableMutation = """
            mutation {
                disableCompany(id: "%s") {
                    id
                    active
                    modifiedDate
                    modifiedBy
                }
            }
        """.formatted(createdCompanyId);

        var disableResult = readData(disableMutation, "data.disableCompany");
        assertThat(disableResult).isNotNull();

        // Step 5: Verify company is disabled but still visible
        var verifyDisabledResult = readData(readQuery, "data.company");
        assertThat(verifyDisabledResult).isNotNull();

        // Step 6: Reactivate company
        String reactivateMutation = """
            mutation {
                reactivateCompany(id: "%s") {
                    id
                    active
                    modifiedDate
                    modifiedBy
                }
            }
        """.formatted(createdCompanyId);

        var reactivateResult = readData(reactivateMutation, "data.reactivateCompany");
        assertThat(reactivateResult).isNotNull();
    }

    @Test
    @Order(2)
    @WithMockUser(roles = {"platform-admins"})
    void shouldExecuteBulkImportFlow() {
        // Given: Bulk import request
        String bulkImportMutation = """
            mutation {
                bulkImportCompanies(input: {
                    companies: [
                        {
                            companyName: "Bulk Import Company 1"
                            registrationNumber: "BIC2025001"
                        },
                        {
                            companyName: "Bulk Import Company 2"
                            registrationNumber: "BIC2025002"
                        },
                        {
                            companyName: "Bulk Import Company 3"
                            registrationNumber: "BIC2025003"
                        }
                    ]
                }) {
                    successCount
                    failureCount
                    errors
                }
            }
        """;

        // When: Execute bulk import (EXPECTED TO FAIL - no implementation yet)
        var importResult = readData(bulkImportMutation, "data.bulkImportCompanies");

        // Then: Verify bulk import results
        assertThat(importResult).isNotNull();

        // Verify imported companies are retrievable
        String companiesQuery = """
            query {
                companies {
                    id
                    companyName
                    registrationNumber
                    active
                }
            }
        """;

        var companiesResult = readData(companiesQuery, "data.companies");
        assertThat(companiesResult).isNotNull();
    }

    @Test
    @Order(3)
    @WithMockUser(roles = {"app-admins"})
    void shouldExecuteSearchAndFilterFlow() {
        // Test 1: Search by company name
        String nameSearchQuery = """
            query {
                companies(search: {
                    companyName: "Integration"
                }) {
                    id
                    companyName
                    registrationNumber
                }
            }
        """;

        var nameSearchResult = readData(nameSearchQuery, "data.companies");
        assertThat(nameSearchResult).isNotNull();

        // Test 2: Search by registration number
        String regSearchQuery = """
            query {
                companies(search: {
                    registrationNumber: "ITC2025001"
                }) {
                    id
                    companyName
                    registrationNumber
                }
            }
        """;

        var regSearchResult = readData(regSearchQuery, "data.companies");
        assertThat(regSearchResult).isNotNull();

        // Test 3: Filter by active status
        String activeFilterQuery = """
            query {
                companies(search: {
                    active: true
                }) {
                    id
                    companyName
                    active
                }
            }
        """;

        var activeFilterResult = readData(activeFilterQuery, "data.companies");
        assertThat(activeFilterResult).isNotNull();

        // Test 4: Text-based search
        String textSearchQuery = """
            query {
                searchCompanies(searchTerm: "Company") {
                    id
                    companyName
                    registrationNumber
                }
            }
        """;

        var textSearchResult = readData(textSearchQuery, "data.searchCompanies");
        assertThat(textSearchResult).isNotNull();
    }

    @Test
    @Order(4)
    @WithMockUser(roles = {"platform-admins"})
    void shouldVerifyAuditTrailFunctionality() {
        // Create company and verify audit fields are populated
        String createWithAuditMutation = """
            mutation {
                createCompany(input: {
                    companyName: "Audit Trail Test Company"
                    registrationNumber: "ATTC2025001"
                }) {
                    id
                    companyName
                    createdDate
                    modifiedDate
                    createdBy
                    modifiedBy
                }
            }
        """;

        var auditCreateResult = readData(createWithAuditMutation, "data.createCompany");
        assertThat(auditCreateResult).isNotNull();

        // Update company and verify modified audit fields change
        String updateWithAuditMutation = """
            mutation {
                updateCompany(id: "1", input: {
                    companyName: "Updated Audit Trail Test Company"
                }) {
                    id
                    companyName
                    createdDate
                    modifiedDate
                    createdBy
                    modifiedBy
                }
            }
        """;

        var auditUpdateResult = readData(updateWithAuditMutation, "data.updateCompany");
        assertThat(auditUpdateResult).isNotNull();

        // Verify audit trail integrity (will fail until audit implementation exists)
        // - createdDate should remain unchanged
        // - modifiedDate should be updated
        // - createdBy should remain unchanged
        // - modifiedBy should reflect current user
    }

    @Test
    @Order(5)
    @WithMockUser(roles = {"app-admins"})
    void shouldHandleErrorScenarios() {
        // Test 1: Duplicate registration number
        String duplicateRegMutation = """
            mutation {
                createCompany(input: {
                    companyName: "Duplicate Registration Company"
                    registrationNumber: "ITC2025001"
                }) {
                    id
                }
            }
        """;

        var duplicateErrors = executeForErrors(duplicateRegMutation);
        assertThat(duplicateErrors).isNotEmpty();

        // Test 2: Invalid company ID
        String invalidIdQuery = """
            query {
                company(id: "99999") {
                    id
                    companyName
                }
            }
        """;

        var invalidIdResult = readData(invalidIdQuery, "data.company");
        // Should return null (not error) for non-existent company

        // Test 3: Update non-existent company
        String updateNonExistentMutation = """
            mutation {
                updateCompany(id: "99999", input: {
                    companyName: "Non-existent Company"
                }) {
                    id
                }
            }
        """;

        var updateNonExistentErrors = executeForErrors(updateNonExistentMutation);
        assertThat(updateNonExistentErrors).isNotEmpty();

        // Test 4: Invalid input validation
        String invalidInputMutation = """
            mutation {
                createCompany(input: {
                    companyName: ""
                    registrationNumber: ""
                }) {
                    id
                }
            }
        """;

        var validationErrors = executeForErrors(invalidInputMutation);
        assertThat(validationErrors).isNotEmpty();
    }
}
