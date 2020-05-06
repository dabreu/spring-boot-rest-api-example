package com.dialogs.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.dialogs.api.service.ICompanyDialogsService;
import com.dialogs.dto.CompanyInformation;
import com.dialogs.dto.DialogInformation;
import com.dialogs.service.CompanyService;

/**
 * Controller providing endpoints to retrieve company and dialog information
 */
@RestController
public class InformationController {

    private final ICompanyDialogsService companyDialogsService;

    public InformationController(CompanyService companyService) {
        this.companyDialogsService = companyService;
    }

    /**
     * Endpoint to retrieve statistics information for the company with the
     * given id
     * 
     * @param id
     *            the id of the company to return information from
     * @return statistics information for the company (# dialogs, most popular
     *         customer)
     */
    @GetMapping("/company/info/{id}")
    public CompanyInformation getCompanyInformation(@PathVariable Long id) {
        return companyDialogsService.getCompanyInformation(id);
    }

    /**
     * Endpoint to retrieve a transcript from a dialog with the given id
     * 
     * @param id
     *            the id of the dialog to retrieve its transcript from
     * @return the transcript information (list of threads for the dialog with
     *         its payload and order)
     */
    @GetMapping("/dialog/info/{id}")
    public DialogInformation getConversationInformation(@PathVariable Long id) {
        return companyDialogsService.getDialogInformation(id);
    }
}
