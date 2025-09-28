package com.systech.nexus.country.repository;

import com.systech.nexus.country.domain.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Country entity operations.
 *
 * MAJOR CHANGES:
 * v1.0 (2025-09-28) - Initial implementation for country name resolution
 *
 * Query Strategy:
 * - Simple lookups by ID and country code
 * - Used primarily for resolving country names in display contexts
 *
 * @author Backend Developer
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    /**
     * Find country by country ID.
     * Used for resolving country names in GraphQL data fetchers.
     *
     * @param countryId the country ID to search for
     * @return Optional containing the country if found
     */
    Optional<Country> findById(Long countryId);

    /**
     * Find country by country code.
     * Used for lookup by ISO country codes.
     *
     * @param countryCode the ISO country code to search for
     * @return Optional containing the country if found
     */
    Optional<Country> findByCountryCode(String countryCode);

    /**
     * Get country name by country ID.
     * Optimized query for display purposes.
     *
     * @param countryId the country ID
     * @return country name if found, null otherwise
     */
    @Query("SELECT c.countryName FROM Country c WHERE c.id = :countryId")
    String findCountryNameById(@Param("countryId") Long countryId);
}