package com.dialogs.service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import javax.transaction.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.dialogs.api.service.DataFilter;
import com.dialogs.api.service.ICompanyDialogsService;
import com.dialogs.dto.CompanyInformation;
import com.dialogs.dto.DialogInformation;
import com.dialogs.model.Company;
import com.dialogs.model.Dialog;
import com.dialogs.repository.CompanyRepository;
import com.dialogs.repository.DialogRepository;

/**
 * Service providing functionality related to companies and their dialogs
 *
 */
@Service
public class CompanyService implements ICompanyDialogsService {

    private static final LocalDate SIGN_UP_IGNORE_PERIOD_START = LocalDate.of(2017, 01, 1);
    private static final LocalDate SIGN_UP_IGNORE_PERIOD_END = LocalDate.of(2017, 11, 30);

    private static final Logger logger = Logger.getLogger(CompanyService.class.getName());

    private CompanyRepository companyRepository;

    private DialogRepository dialogRepository;

    private Collection<DataFilter> dialogFilters;

    public CompanyService(CompanyRepository companyRepository, DialogRepository dialogRepository, Collection<DataFilter> dialogFilters) {
        this.companyRepository = companyRepository;
        this.dialogRepository = dialogRepository;
        this.dialogFilters = dialogFilters;
    }

    /**
     * Method to import all the information for companies and their dialogs
     * 
     * @param companies
     */
    @Override
    @Transactional
    public void importCompaniesDialogs(List<Company> companies) {
        try {
            companies.stream().filter(company -> shouldImport(company)).forEach(company -> importCompanyDialogs(company));
        } catch (Exception e) {
            logger.severe("Companies could not be imported. Exception: " + e.getMessage());
            throw new CompanyServiceException("Companies could not be imported. Exception: " + e.getMessage());
        }
    }

    /**
     * Returns the statistics information for the company with the given id
     * 
     * @param companyId
     *            the id of the company to return information from
     * @return statistics information for the company (# dialogs, most popular
     *         customer)
     */
    @Override
    public CompanyInformation getCompanyInformation(Long companyId) {
        Company company = findCompany(companyId);
        Long dialogCount = dialogRepository.countByCompany(company);
        Long mostPopularCustomer = getMostPopularCustomer(company);
        return new CompanyInformation(company.getName(), dialogCount, mostPopularCustomer);
    }

    /**
     * Returns a transcript from a dialog with the given id
     * 
     * @param id
     *            the id of the dialog to retrieve its transcript from
     * @return the transcript information (list of threads for the dialog with
     *         its payload and order)
     */
    @Override
    public DialogInformation getDialogInformation(Long dialogId) {
        Dialog dialog = findDialogWithThreads(dialogId);
        return new DialogInformation(dialog);
    }

    /**
     * Indicates whether the given company should be imported
     * 
     * @param company
     * @return
     */
    private boolean shouldImport(Company company) {
        return !(company.isSignUpBetween(SIGN_UP_IGNORE_PERIOD_START, SIGN_UP_IGNORE_PERIOD_END) && company.hasDialogs());
    }

    /**
     * Import a company/dialogs, applying the filters before persisting
     * 
     * @param company
     */
    private void importCompanyDialogs(Company company) {
        applyFilters(company);
        save(company);
    }

    /**
     * Applies filtering to the dialogs/threads of the given company. It might
     * modify the information of the indicated company according to the
     * filtering rules
     * 
     * @param company
     */
    private void applyFilters(Company company) {
        dialogFilters.forEach(filter -> filter.apply(company));
    }

    /**
     * Persists the company and all of its related information
     * 
     * @param company
     */
    private void save(Company company) {
        companyRepository.save(company);
    }

    /**
     * Finds the company by its external id
     * 
     * @param id
     * @return
     */
    private Company findCompany(Long id) {
        Optional<Company> company = companyRepository.findByExternalId(id);
        return company.orElseThrow(() -> new CompanyNotFoundException("Company not found. Id: " + id));
    }

    /**
     * Finds the dialog by its external id also loading all its threads
     * 
     * @param id
     *            the id of the dialog
     * @return
     */
    private Dialog findDialogWithThreads(Long id) {
        Optional<Dialog> dialog = dialogRepository.findWithThreads(id);
        return dialog.orElseThrow(() -> new DialogNotFoundException("Dialog not found. Id: " + id));
    }

    /**
     * Returns the id of the customer that occurs the most on company's dialogs
     * 
     * @param company
     * @return
     */
    private Long getMostPopularCustomer(Company company) {
        List<Long> mostPopularCustomer = dialogRepository.customerWithMostDialogs(company, PageRequest.of(0, 1, Sort.unsorted()));
        return (mostPopularCustomer.isEmpty() ? null : mostPopularCustomer.get(0));
    }
}
