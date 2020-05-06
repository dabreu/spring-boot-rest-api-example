package com.dialogs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dialogs.model.DialogThreadConflict;

/**
 * Repository for Dialog Thread with conflict model
 */
@Repository
public interface DialogThreadConflictRepository extends JpaRepository<DialogThreadConflict, Long> {
}