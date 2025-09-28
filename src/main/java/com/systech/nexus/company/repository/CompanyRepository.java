package com.systech.nexus.company.repository;

import com.systech.nexus.company.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Company entity operations.
 *
 * MAJOR CHANGES:
 * v1.0 (2025-09-27) - Initial CRUD operations and search capabilities
 *
 * Query Strategy:
 * - Company name searches are case-insensitive for better user experience
 * - Registration number searches are case-sensitive for exact matching
 * - Text search covers both company name and registration number
 * - Active status filtering for soft delete support
 * - Ordering by company name for consistent results
 *
 * Performance Considerations:
 * - Uses database indexes on companyName, registrationNumber, and active fields
 * - JPQL queries optimized for PostgreSQL
 * - Case sensitivity rules minimize index scanning
 *
 * Business Rules:
 * - Registration numbers must be unique (enforced at database level)
 * - Soft delete pattern: inactive companies remain queryable but hidden from normal lists
 * - Search operations are case-insensitive except for registration number exact match
 *
 * @author Backend Developer
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    /**
     * Find company by company code (case-sensitive exact match).
     * Company codes are unique business identifiers.
     *
     * @param companyCode the exact company code to search for
     * @return Optional containing the company if found
     */
    Optional<Company> findByCompanyCode(String companyCode);

    /**
     * Check if a company code already exists (case-sensitive).
     * Used for uniqueness validation during company code generation.
     *
     * @param companyCode the company code to check
     * @return true if company code exists, false otherwise
     */
    boolean existsByCompanyCode(String companyCode);

    /**
     * Find companies by company status.
     * Used to filter companies by their current status.
     *
     * @param status the company status to filter by
     * @return list of companies with the specified status, ordered by company name
     */
    @Query("SELECT c FROM Company c WHERE c.companyStatus = :status ORDER BY c.companyName")
    List<Company> findByCompanyStatus(@Param("status") Company.CompanyStatus status);

    /**
     * Find companies by company name containing search term (case-insensitive).
     * Supports partial matching for flexible search.
     *
     * @param companyName the search term to find in company names
     * @return list of companies with names containing the search term
     */
    @Query("SELECT c FROM Company c WHERE LOWER(c.companyName) LIKE LOWER(CONCAT('%', :companyName, '%')) ORDER BY c.companyName")
    List<Company> findByCompanyNameContainingIgnoreCase(@Param("companyName") String companyName);

    /**
     * Find companies by company name containing search term and company status.
     * Combines name search with status filtering.
     *
     * @param companyName the search term to find in company names
     * @param status      the company status to filter by
     * @return list of companies matching both criteria
     */
    @Query("SELECT c FROM Company c WHERE LOWER(c.companyName) LIKE LOWER(CONCAT('%', :companyName, '%')) " +
           "AND c.companyStatus = :status ORDER BY c.companyName")
    List<Company> findByCompanyNameContainingIgnoreCaseAndStatus(@Param("companyName") String companyName,
                                                                @Param("status") Company.CompanyStatus status);

    /**
     * Find companies by company code containing search term and status.
     * Supports partial company code search with status filtering.
     *
     * @param companyCode the search term to find in company codes
     * @param status      the company status to filter by
     * @return list of companies matching both criteria
     */
    @Query("SELECT c FROM Company c WHERE LOWER(c.companyCode) LIKE LOWER(CONCAT('%', :companyCode, '%')) " +
           "AND c.companyStatus = :status ORDER BY c.companyName")
    List<Company> findByCompanyCodeContainingIgnoreCaseAndStatus(@Param("companyCode") String companyCode,
                                                                @Param("status") Company.CompanyStatus status);

    /**
     * Search companies by text across company name and company code.
     * Performs comprehensive text search with case-insensitive matching.
     *
     * @param searchTerm the text to search for in company name or company code
     * @return list of companies matching the search term in either field
     */
    @Query("SELECT c FROM Company c WHERE " +
           "LOWER(c.companyName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.companyCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY c.companyName")
    List<Company> searchByText(@Param("searchTerm") String searchTerm);

    /**
     * Search companies by text across company name and company code.
     * Combines comprehensive text search with status filtering.
     *
     * @param searchTerm the text to search for in company name or company code
     * @param status     the company status to filter by
     * @return list of companies matching the search term
     */
    @Query("SELECT c FROM Company c WHERE " +
           "(LOWER(c.companyName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.companyCode) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND c.companyStatus = :status " +
           "ORDER BY c.companyName")
    List<Company> searchByTextAndStatus(@Param("searchTerm") String searchTerm, @Param("status") Company.CompanyStatus status);

    /**
     * Find all companies ordered by creation date (newest first).
     * Used for administrative views showing recent company registrations.
     *
     * @return list of all companies ordered by creation date descending
     */
    @Query("SELECT c FROM Company c ORDER BY c.createdAt DESC")
    List<Company> findAllOrderByCreatedDateDesc();

    /**
     * Find all active companies ordered by company name.
     * Used for standard company listings in the application.
     *
     * @return list of active companies ordered alphabetically by name
     */
    @Query("SELECT c FROM Company c WHERE c.companyStatus = 'ACTIVE' ORDER BY c.companyName")
    List<Company> findAllActiveOrderByName();

    /**
     * Advanced search with multiple optional criteria.
     * Supports combined filtering by company name, company code, and status.
     * All parameters are optional - null values are ignored in the search.
     *
     * @param companyName optional company name search term
     * @param companyCode optional company code search term
     * @param status      optional company status filter
     * @return list of companies matching all provided criteria
     */
    @Query("SELECT c FROM Company c WHERE " +
           "(:companyName IS NULL OR LOWER(c.companyName) LIKE LOWER(CONCAT('%', :companyName, '%'))) AND " +
           "(:companyCode IS NULL OR LOWER(c.companyCode) LIKE LOWER(CONCAT('%', :companyCode, '%'))) AND " +
           "(:status IS NULL OR c.companyStatus = :status) " +
           "ORDER BY c.companyName")
    List<Company> findBySearchCriteria(@Param("companyName") String companyName,
                                      @Param("companyCode") String companyCode,
                                      @Param("status") Company.CompanyStatus status);
}