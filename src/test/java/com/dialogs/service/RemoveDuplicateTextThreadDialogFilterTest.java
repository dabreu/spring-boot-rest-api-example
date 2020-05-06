package com.dialogs.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.dialogs.TestUtils;
import com.dialogs.model.Company;
import com.dialogs.model.Dialog;
import com.dialogs.model.DialogThread;

public class RemoveDuplicateTextThreadDialogFilterTest {

    @Test
    public void testDuplicateTextThreadsFilterWithNoDialogs() {
        Company company = TestUtils.createCompany(1L);
        assertFalse(company.hasDialogs());
        RemoveDuplicateTextThreadDialogFilter filter = new RemoveDuplicateTextThreadDialogFilter();
        filter.apply(company);
        assertFalse(company.hasDialogs());
    }

    @Test
    public void testDuplicateTextThreadsFilterWithNoThreads() {
        Company company = TestUtils.createCompany(1L, TestUtils.createDialog(1L));
        assertEquals(1, company.getDialogs().size());
        assertTrue(company.getDialogs().get(0).getThreads().isEmpty());
        RemoveDuplicateTextThreadDialogFilter filter = new RemoveDuplicateTextThreadDialogFilter();
        filter.apply(company);
        assertEquals(1, company.getDialogs().size());
        assertTrue(company.getDialogs().get(0).getThreads().isEmpty());
    }

    @Test
    public void testDuplicateTextThreadsAreRemovedFromDialogAndOnlyTheMostRecentOneIsLeft() {
        DialogThread[] threads = new DialogThread[4];
        threads[0] = TestUtils.createThread(1L, "duplicated payload");
        threads[1] = TestUtils.createThread(2L, "payload #2");
        threads[2] = TestUtils.createThread(3L, "payload #3");
        threads[3] = TestUtils.createThread(4L, "duplicated payload");
        Dialog dialog = TestUtils.createDialog(1L, threads);
        Company company = TestUtils.createCompany(1L, dialog);

        RemoveDuplicateTextThreadDialogFilter filter = new RemoveDuplicateTextThreadDialogFilter();
        filter.apply(company);

        TestUtils.verifyDialogContainsThread(company, 1L, 1L);
        TestUtils.verifyDialogContainsThread(company, 1L, 2L);
        TestUtils.verifyDialogContainsThread(company, 1L, 3L);
        TestUtils.verifyDialogDoesNotContainThread(company, 1L, 4L);
    }

    @Test
    public void testDuplicateTextThreadsAreRemovedFromEachDialogIndependently() {
        DialogThread[] threads = new DialogThread[4];
        threads[0] = TestUtils.createThread(1L, "duplicated payload");
        threads[1] = TestUtils.createThread(2L, "payload #2");
        threads[2] = TestUtils.createThread(3L, "payload #3");
        threads[3] = TestUtils.createThread(4L, "duplicated payload");
        Dialog dialog1 = TestUtils.createDialog(1L, threads);

        threads = new DialogThread[4];
        threads[0] = TestUtils.createThread(1L, "duplicated payload");
        threads[1] = TestUtils.createThread(2L, "payload #2");
        threads[2] = TestUtils.createThread(3L, "duplicated payload");
        threads[3] = TestUtils.createThread(4L, "payload #4");
        Dialog dialog2 = TestUtils.createDialog(2L, threads);

        Company company = TestUtils.createCompany(1L, dialog1, dialog2);

        RemoveDuplicateTextThreadDialogFilter filter = new RemoveDuplicateTextThreadDialogFilter();
        filter.apply(company);

        TestUtils.verifyDialogContainsThread(company, 1L, 1L);
        TestUtils.verifyDialogContainsThread(company, 1L, 2L);
        TestUtils.verifyDialogContainsThread(company, 1L, 3L);
        TestUtils.verifyDialogDoesNotContainThread(company, 1L, 4L);

        TestUtils.verifyDialogContainsThread(company, 2L, 1L);
        TestUtils.verifyDialogContainsThread(company, 2L, 2L);
        TestUtils.verifyDialogDoesNotContainThread(company, 2L, 3L);
        TestUtils.verifyDialogContainsThread(company, 2L, 4L);
    }
}
