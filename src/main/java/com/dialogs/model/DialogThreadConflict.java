package com.dialogs.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Class modeling a Thread with conflict
 *
 */
@Table
@Entity
public class DialogThreadConflict {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long primaryId;

    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private Long dialogId;

    @Column(columnDefinition = "VARCHAR", nullable = false)
    private String payload;

    public DialogThreadConflict() {
    }

    public DialogThreadConflict(DialogThread thread) {
        this.id = thread.getId();
        this.dialogId = thread.getDialog().getId();
        this.payload = thread.getPayload();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

}
