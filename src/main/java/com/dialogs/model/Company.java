package com.dialogs.model;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Class modeling a Company and its dialogs
 *
 */
@Entity
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long primaryId;

    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate signedUp;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dialog> dialogs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getSignedUp() {
        return signedUp;
    }

    public void setSignedUp(LocalDate signedUp) {
        this.signedUp = signedUp;
    }

    public List<Dialog> getDialogs() {
        return dialogs;
    }

    public void setDialogs(List<Dialog> dialogs) {
        this.dialogs = (dialogs == null ? Collections.emptyList() : dialogs);
        this.dialogs.stream().forEach(dialog -> dialog.setCompany(this));
    }

    public boolean isSignUpBetween(LocalDate start, LocalDate end) {
        return (signedUp != null && !signedUp.isBefore(start) && !signedUp.isAfter(end));
    }

    public boolean hasDialogs() {
        return (dialogs != null && !dialogs.isEmpty());
    }
}
