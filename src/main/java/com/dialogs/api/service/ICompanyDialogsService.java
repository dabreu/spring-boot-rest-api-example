package com.dialogs.api.service;

import java.util.List;

import com.dialogs.dto.CompanyInformation;
import com.dialogs.dto.DialogInformation;
import com.dialogs.model.Company;

/**
 * Service interface with functionality for handling information of companies
 * and their dialogs
 */
public interface ICompanyDialogsService {

    /**
     * Method to import all the information for companies and their dialogs,
     * applying before persisting them the filters implemented according to the
     * existing business rules
     * 
     * @param companies
     *            the list of companies to import
     */
    public void importCompaniesDialogs(List<Company> companies);

    /**
     * Returns the statistics information for the company with the given id
     * 
     * @param companyId
     *            the id of the company to return information from
     * @return statistics information for the company (# dialogs, most popular
     *         customer)
     */
    public CompanyInformation getCompanyInformation(Long companyId);

    /**
     * Returns information about the given dialog
     * 
     * @param dialogId
     * @return
     */
    public DialogInformation getDialogInformation(Long dialogId);

}