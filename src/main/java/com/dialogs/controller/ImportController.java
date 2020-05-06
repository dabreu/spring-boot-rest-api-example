package com.dialogs.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dialogs.api.service.ICompanyDialogsService;
import com.dialogs.model.Company;
import com.dialogs.service.CompanyService;

/**
 * Controller providing endpoint to import companies and its dialogs and threads
 * information
 */
@RestController
public class ImportController {

    private final ICompanyDialogsService companyDialogsService;

    public ImportController(CompanyService companyService) {
        this.companyDialogsService = companyService;
    }

    /**
     * Endpoint to import a list of companies dialogs with their threads
     * information.
     * 
     * @param companies
     *            the list of companies to import
     */
    @PostMapping("/company/import")
    public void importCompanies(@RequestBody List<Company> companies) {
        companyDialogsService.importCompaniesDialogs(companies);
    }
}
