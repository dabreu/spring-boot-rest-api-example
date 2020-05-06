package com.dialogs.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dialogs.model.Company;

/**
 * Repository for Company model
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    /**
     * Finds a company by its external id
     * 
     * @param id
     * @return
     */
    @Query("SELECT c FROM Company c WHERE c.id = :id")
    public Optional<Company> findByExternalId(@Param("id") Long id);
}