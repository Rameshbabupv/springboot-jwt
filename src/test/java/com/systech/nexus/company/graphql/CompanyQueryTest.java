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
 * GraphQL Contract Test for Company Query Operations.
 *
 * MAJOR CHANGES:
 * v1.0 (2025-09-27) - Initial contract tests for companies GraphQL queries
 *
 * These tests verify the GraphQL schema compliance and query functionality:
 * - companies query with optional search parameters
 * - company(id) query for single company retrieval
 * - searchCompanies query for text-based search
 *
 * All tests require app-admins or platform-admins authentication as per specification.
 * These tests MUST FAIL initially (TDD approach) until implementation is complete.
 *
 * @author Backend Developer
 * @version 1.0
 * @since 1.0
 */
@SpringBootTest
@ActiveProfiles("test")
class CompanyQueryTest {

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
    void shouldExecuteCompaniesQueryWithoutSearch() {
        // Given: GraphQL query for all companies
        String query = """
            query {
                companies {
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
        """;

        // When: Execute query (EXPECTED TO FAIL - no implementation yet)
        // Then: Should return valid GraphQL response structure
        var result = readData(query, "data.companies");

        // Verify response structure (will fail until CompanyDataFetcher is implemented)
        assertThat(result).isNotNull();
    }

    @Test
    @WithMockUser(roles = {"platform-admins"})
    void shouldExecuteCompaniesQueryWithSearchFilter() {
        // Given: GraphQL query with search parameters
        String query = """
            query {
                companies(search: {
                    companyName: "Tech"
                    active: true
                }) {
                    id
                    companyName
                    registrationNumber
                    active
                }
            }
        """;

        // When: Execute query (EXPECTED TO FAIL - no implementation yet)
        // Then: Should handle search parameters correctly
        var result = readData(query, "data.companies");

        // Verify response structure (will fail until CompanyDataFetcher is implemented)
        assertThat(result).isNotNull();
    }

    @Test
    @WithMockUser(roles = {"app-admins"})
    void shouldExecuteCompanyByIdQuery() {
        // Given: GraphQL query for specific company
        String query = """
            query {
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
        """;

        // When: Execute query (EXPECTED TO FAIL - no implementation yet)
        // Then: Should return company or null
        var result = readData(query, "data.company");

        // Verify response can handle single company query (will fail until implementation exists)
        assertThat(result).isNotNull();
    }

    @Test
    @WithMockUser(roles = {"platform-admins"})
    void shouldExecuteSearchCompaniesQuery() {
        // Given: GraphQL search query
        String query = """
            query {
                searchCompanies(searchTerm: "Solutions") {
                    id
                    companyName
                    registrationNumber
                    active
                }
            }
        """;

        // When: Execute query (EXPECTED TO FAIL - no implementation yet)
        // Then: Should return search results
        var result = readData(query, "data.searchCompanies");

        // Verify search functionality (will fail until CompanyDataFetcher is implemented)
        assertThat(result).isNotNull();
    }

    @Test
    void shouldRejectUnauthorizedCompaniesQuery() {
        // Given: GraphQL query without authentication
        String query = """
            query {
                companies {
                    id
                    companyName
                }
            }
        """;

        // When: Execute query without proper authentication
        // Then: Should be rejected with authentication error
        var result = executeForErrors(query);

        // Verify security restrictions are enforced
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getMessage()).contains("Access Denied");
    }

    @Test
    @WithMockUser(roles = {"basic-users"})
    void shouldRejectInsufficientRoleForCompaniesQuery() {
        // Given: GraphQL query with insufficient role
        String query = """
            query {
                companies {
                    id
                    companyName
                }
            }
        """;

        // When: Execute query with basic-users role (insufficient)
        // Then: Should be rejected with authorization error
        var result = executeForErrors(query);

        // Verify role-based access control
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getMessage()).contains("Access Denied");
    }
}
