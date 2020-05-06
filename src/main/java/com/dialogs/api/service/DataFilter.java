package com.dialogs.api.service;

import com.dialogs.model.Company;

/**
 * Interface to model filters to be applied to company's data
 */
public interface DataFilter {

    /**
     * Applies the filter definition to the company's data
     * 
     * @param company
     *            the company where the filter will be applied to
     */
    public void apply(Company company);

}