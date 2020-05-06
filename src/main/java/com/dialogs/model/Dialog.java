package com.dialogs.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Class modeling a Company's dialog and its threads
 *
 */
@Table
@Entity
public class Dialog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long primaryId;

    @Column(unique = true, nullable = false)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;

    @Column(nullable = false)
    private Long number;

    @Column(name = "CUSTOMER_ID", nullable = false)
    private Long userId;

    @Column(name = "CUSTOMER_EMAIL", nullable = false)
    private String from;

    @Column(nullable = false)
    private LocalDateTime received;

    @OneToMany(mappedBy = "dialog", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "POSITION")
    private List<DialogThread> threads;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public LocalDateTime getReceived() {
        return received;
    }

    public void setReceived(LocalDateTime received) {
        this.received = received;
    }

    public List<DialogThread> getThreads() {
        return threads;
    }

    public void setThreads(List<DialogThread> threads) {
        this.threads = (threads == null ? Collections.emptyList() : threads);
        this.threads.stream().forEach(thread -> thread.setDialog(this));
    }
}