package com.dialogs.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dialogs.model.Company;
import com.dialogs.model.Dialog;

/**
 * Repository for Dialog model
 */
@Repository
public interface DialogRepository extends JpaRepository<Dialog, Long> {

    /**
     * Returns the number of dialogs of the indicated company
     * 
     * @param company
     *            the company to count its dialogs
     * @return
     */
    @Query("SELECT COUNT(c) FROM Dialog c WHERE c.company = :company")
    public Long countByCompany(@Param("company") Company company);

    /**
     * Returns the customer most appearing on the company's dialogs
     * 
     * @param company
     *            the company to calculate its customer most appearing on its
     *            dialogs
     * @return
     */
    @Query("SELECT c.userId FROM Dialog c WHERE c.company = :company GROUP BY c.userId ORDER BY COUNT(*) DESC")
    public List<Long> customerWithMostDialogs(@Param("company") Company company, Pageable pageable);

    /**
     * Returns the dialog for the given id and its threads
     * 
     * @param id
     *            the dialog id
     * @return the dialog entity with its threads loaded
     */
    @Query("SELECT c FROM Dialog c LEFT JOIN FETCH c.threads WHERE c.id = :id")
    public Optional<Dialog> findWithThreads(@Param("id") Long id);
}