package com.dialogs.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.dialogs.TestUtils;
import com.dialogs.model.Company;
import com.dialogs.model.Dialog;
import com.dialogs.model.DialogThread;
import com.dialogs.model.DialogThreadConflict;
import com.dialogs.repository.DialogThreadConflictRepository;

public class RemoveDuplicateIdThreadDialogFilterTest {

    private DialogThreadConflictRepository repository;
    private RemoveDuplicateIdThreadDialogFilter filter;

    @Before
    public void beforeEach() {
        repository = Mockito.mock(DialogThreadConflictRepository.class);
        filter = new RemoveDuplicateIdThreadDialogFilter(repository);
    }

    @Test
    public void testDuplicateIdThreadsFilterWithNoDialogs() {
        Company company = TestUtils.createCompany(1L);
        assertFalse(company.hasDialogs());
        filter.apply(company);
        assertFalse(company.hasDialogs());
    }

    @Test
    public void testDuplicateIdThreadsFilterWithNoThreads() {
        Company company = TestUtils.createCompany(1L, TestUtils.createDialog(1L));
        assertEquals(1, company.getDialogs().size());
        assertTrue(company.getDialogs().get(0).getThreads().isEmpty());
        filter.apply(company);
        assertEquals(1, company.getDialogs().size());
        assertTrue(company.getDialogs().get(0).getThreads().isEmpty());
    }

    @Test
    public void testDuplicateIdThreadsAreRemovedFromDialog() {
        DialogThread[] threads = new DialogThread[4];
        threads[0] = TestUtils.createThread(1L, "payload #1");
        threads[1] = TestUtils.createThread(3L, "payload #2");
        threads[2] = TestUtils.createThread(3L, "payload #3");
        threads[3] = TestUtils.createThread(4L, "payload #4");
        Dialog dialog = TestUtils.createDialog(1L, threads);
        Company company = TestUtils.createCompany(1L, dialog);

        filter.apply(company);

        TestUtils.verifyDialogContainsThread(company, 1L, 1L);
        TestUtils.verifyDialogContainsThread(company, 1L, 4L);
        TestUtils.verifyDialogDoesNotContainThread(company, 1L, 3L);
    }

    @Test
    public void testDuplicateIdThreadsAreRemovedFromEachDialogIndependently() {
        DialogThread[] threads = new DialogThread[4];
        threads[0] = TestUtils.createThread(1L, "duplicated payload");
        threads[1] = TestUtils.createThread(2L, "payload #2");
        threads[2] = TestUtils.createThread(2L, "payload #3");
        threads[3] = TestUtils.createThread(4L, "duplicated payload");
        Dialog dialog1 = TestUtils.createDialog(1L, threads);

        threads = new DialogThread[4];
        threads[0] = TestUtils.createThread(1L, "duplicated payload");
        threads[1] = TestUtils.createThread(2L, "payload #2");
        threads[2] = TestUtils.createThread(3L, "duplicated payload");
        threads[3] = TestUtils.createThread(3L, "payload #4");
        Dialog dialog2 = TestUtils.createDialog(2L, threads);

        Company company = TestUtils.createCompany(1L, dialog1, dialog2);

        filter.apply(company);

        TestUtils.verifyDialogContainsThread(company, 1L, 1L);
        TestUtils.verifyDialogContainsThread(company, 1L, 4L);
        TestUtils.verifyDialogDoesNotContainThread(company, 1L, 2L);

        TestUtils.verifyDialogContainsThread(company, 2L, 1L);
        TestUtils.verifyDialogContainsThread(company, 2L, 2L);
        TestUtils.verifyDialogDoesNotContainThread(company, 2L, 3L);
    }

    @Test
    public void testDuplicateIdThreadsAreSavedAsConflictThreads() {
        DialogThread[] threads = new DialogThread[4];
        threads[0] = TestUtils.createThread(1L, "payload #1");
        threads[1] = TestUtils.createThread(3L, "payload #2");
        threads[2] = TestUtils.createThread(3L, "payload #3");
        threads[3] = TestUtils.createThread(4L, "payload #4");
        Dialog dialog = TestUtils.createDialog(1L, threads);
        Company company = TestUtils.createCompany(1L, dialog);

        ArgumentCaptor<DialogThreadConflict> argCaptor = ArgumentCaptor.forClass(DialogThreadConflict.class);

        filter.apply(company);

        TestUtils.verifyDialogContainsThread(company, 1L, 1L);
        TestUtils.verifyDialogContainsThread(company, 1L, 4L);
        TestUtils.verifyDialogDoesNotContainThread(company, 1L, 3L);

        verify(repository, times(2)).save(argCaptor.capture());
        List<DialogThreadConflict> capturedThreads = argCaptor.getAllValues();
        assertEquals("payload #2", capturedThreads.get(0).getPayload());
        assertEquals("payload #3", capturedThreads.get(1).getPayload());
    }
}
