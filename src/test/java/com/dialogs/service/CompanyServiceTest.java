package com.dialogs.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.dialogs.TestUtils;
import com.dialogs.api.service.DataFilter;
import com.dialogs.model.Company;
import com.dialogs.model.Dialog;
import com.dialogs.model.DialogThread;
import com.dialogs.repository.CompanyRepository;
import com.dialogs.repository.DialogRepository;

public class CompanyServiceTest {

    private CompanyRepository repository;
    private DialogRepository dialogRepository;
    private DataFilter filter;
    private CompanyService service;

    @Before
    public void beforeEach() {
        repository = mock(CompanyRepository.class);
        dialogRepository = mock(DialogRepository.class);
        filter = mock(DataFilter.class);
        List<DataFilter> filters = new ArrayList<DataFilter>();
        filters.add(filter);
        service = new CompanyService(repository, dialogRepository, filters);
    }

    @Test
    public void testFilteringAndPersistenceAreCalledWhenCompanyCanBeImported() {
        DialogThread[] threads = new DialogThread[3];
        threads[0] = TestUtils.createThread(1L, "payload #1");
        threads[1] = TestUtils.createThread(2L, "payload #2");
        threads[2] = TestUtils.createThread(3L, "payload #3");
        Dialog dialog1 = TestUtils.createDialog(1L, threads);
        threads = new DialogThread[2];
        threads[0] = TestUtils.createThread(1L, "payload #1");
        threads[1] = TestUtils.createThread(2L, "payload #2");
        Dialog dialog2 = TestUtils.createDialog(2L, threads);

        Company company = TestUtils.createCompany(1L, dialog1, dialog2);
        List<Company> companies = new ArrayList<Company>();
        companies.add(company);

        ArgumentCaptor<Company> argCaptor = ArgumentCaptor.forClass(Company.class);

        service.importCompaniesDialogs(companies);

        verify(repository, times(1)).save(argCaptor.capture());
        verify(filter, times(1)).apply(any(Company.class));

        List<Company> capturedCompanies = argCaptor.getAllValues();
        assertEquals(Long.valueOf(1), capturedCompanies.get(0).getId());
    }

    @Test
    public void testFilteringAndPersistenceAreNotCalledWhenCompanyShouldBeIgnoredBecauseOfSignUpDate() {
        DialogThread[] threads = new DialogThread[3];
        threads[0] = TestUtils.createThread(1L, "payload #1");
        threads[1] = TestUtils.createThread(2L, "payload #2");
        threads[2] = TestUtils.createThread(3L, "payload #3");
        Dialog dialog1 = TestUtils.createDialog(1L, threads);
        threads = new DialogThread[2];
        threads[0] = TestUtils.createThread(1L, "payload #1");
        threads[1] = TestUtils.createThread(2L, "payload #2");
        Dialog dialog2 = TestUtils.createDialog(2L, threads);

        Company company = TestUtils.createCompany(1L, dialog1, dialog2);
        company.setSignedUp(LocalDate.of(2017, 1, 20));
        List<Company> companies = new ArrayList<Company>();
        companies.add(company);

        ArgumentCaptor<Company> argCaptor = ArgumentCaptor.forClass(Company.class);

        service.importCompaniesDialogs(companies);

        verify(repository, times(0)).save(argCaptor.capture());
        verify(filter, times(0)).apply(any(Company.class));
    }

    @Test
    public void testCompanyServiceExceptionIsThrownInCaseOfPersistenceError() {
        DialogThread[] threads = new DialogThread[3];
        threads[0] = TestUtils.createThread(1L, "payload #1");
        threads[1] = TestUtils.createThread(2L, "payload #2");
        threads[2] = TestUtils.createThread(3L, "payload #3");
        Dialog dialog1 = TestUtils.createDialog(1L, threads);
        threads = new DialogThread[2];
        threads[0] = TestUtils.createThread(1L, "payload #1");
        threads[1] = TestUtils.createThread(2L, "payload #2");
        Dialog dialog2 = TestUtils.createDialog(2L, threads);

        Company company = TestUtils.createCompany(1L, dialog1, dialog2);
        List<Company> companies = new ArrayList<Company>();
        companies.add(company);

        when(repository.save(any(Company.class))).thenThrow(new RuntimeException());

        try {
            service.importCompaniesDialogs(companies);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof CompanyServiceException);
        }
    }

    @Test
    public void testIfExceptionIsThrownWhilePersistingDataThenImportIsAborted() {
        DialogThread[] threads = new DialogThread[3];
        threads[0] = TestUtils.createThread(1L, "payload #1");
        threads[1] = TestUtils.createThread(2L, "payload #2");
        threads[2] = TestUtils.createThread(3L, "payload #3");
        Dialog dialog1 = TestUtils.createDialog(1L, threads);
        threads = new DialogThread[2];
        threads[0] = TestUtils.createThread(1L, "payload #1");
        threads[1] = TestUtils.createThread(2L, "payload #2");
        Dialog dialog2 = TestUtils.createDialog(2L, threads);

        Company company1 = TestUtils.createCompany(1L, dialog1, dialog2);
        Company company2 = TestUtils.createCompany(2L, dialog1, dialog2);

        List<Company> companies = new ArrayList<Company>();
        companies.add(company1);
        companies.add(company2);

        ArgumentCaptor<Company> argCaptor = ArgumentCaptor.forClass(Company.class);

        when(repository.save(any(Company.class))).thenThrow(new RuntimeException());

        try {
            service.importCompaniesDialogs(companies);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof CompanyServiceException);
        }

        verify(repository, times(1)).save(argCaptor.capture());

        List<Company> capturedCompanies = argCaptor.getAllValues();
        assertEquals(Long.valueOf(1), capturedCompanies.get(0).getId());
    }
}
