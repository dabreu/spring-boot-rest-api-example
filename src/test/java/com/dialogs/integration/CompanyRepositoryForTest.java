package com.dialogs.integration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dialogs.model.Company;

/**
 * This repository is only intended to be used by tests
 */
@Repository
public interface CompanyRepositoryForTest extends JpaRepository<Company, Long> {

    /**
     * This method returns the company with its full tree of dialogs and their
     * corresponding threads. Make the name explicit so it is used only when
     * really needed
     * 
     * @param id
     * @return
     */
    @Query("SELECT c  FROM Company c JOIN FETCH c.dialogs t JOIN FETCH t.threads WHERE c.id = :id")
    public Company findAllDialogsAndThreads(@Param("id") Long id);
}