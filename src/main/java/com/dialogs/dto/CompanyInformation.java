package com.dialogs.dto;

/**
 * DTO containing company statistics information
 */
public class CompanyInformation {

    private String name;
    private Long dialogsCount;
    private Long mostPopularCustomer;

    public CompanyInformation() {
    }

    public CompanyInformation(String name, Long dialogsCount, Long mostPopularCustomer) {
        this.name = name;
        this.dialogsCount = dialogsCount;
        this.mostPopularCustomer = mostPopularCustomer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDialogsCount() {
        return dialogsCount;
    }

    public void setDialogsCount(Long dialogsCount) {
        this.dialogsCount = dialogsCount;
    }

    public Long getMostPopularCustomer() {
        return mostPopularCustomer;
    }

    public void setMostPopularCustomer(Long mostPopularCustomer) {
        this.mostPopularCustomer = mostPopularCustomer;
    }
}
