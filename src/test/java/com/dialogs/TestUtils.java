package com.dialogs;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import com.dialogs.model.Company;
import com.dialogs.model.Dialog;
import com.dialogs.model.DialogThread;

/**
 * Class with utility methods for tests
 */
public class TestUtils {

    public static Company createCompany(Long id, Dialog... dialogs) {
        Company company = new Company();
        company.setId(id);
        if (dialogs != null) {
            company.setDialogs(Arrays.asList(dialogs));
        }
        return company;
    }

    public static Company createCompany(Long id, String name, LocalDate signUp, Dialog... dialogs) {
        Company company = new Company();
        company.setId(id);
        company.setName(name);
        company.setSignedUp(signUp);
        if (dialogs != null) {
            company.setDialogs(Arrays.asList(dialogs));
        }
        return company;
    }

    public static Dialog createDialog(Long id, DialogThread... threads) {
        Dialog dialog = new Dialog();
        dialog.setId(id);
        if (threads != null) {
            dialog.setThreads(Arrays.asList(threads));
        }
        return dialog;
    }

    public static Dialog createDialog(Long id, Long number, Long userId, String from, LocalDateTime received, DialogThread... threads) {
        Dialog dialog = new Dialog();
        dialog.setId(id);
        dialog.setNumber(number);
        dialog.setUserId(userId);
        dialog.setFrom(from);
        dialog.setReceived(received);
        if (threads != null) {
            dialog.setThreads(Arrays.asList(threads));
        }
        return dialog;
    }

    public static DialogThread createThread(Long id, String payload) {
        DialogThread thread = new DialogThread();
        thread.setId(id);
        thread.setPayload(payload);
        return thread;
    }

    public static void verifyDialogContainsThread(Company company, Long dialogId, Long threadId) {
        Dialog dialog = company.getDialogs().stream().filter(conv -> conv.getId().equals(dialogId)).findFirst().get();
        assertNotNull(dialog);
        DialogThread thread = dialog.getThreads().stream().filter(aThread -> aThread.getId().equals(threadId)).findFirst().get();
        assertNotNull(thread);
    }

    public static void verifyDialogDoesNotContainThread(Company company, Long dialogId, Long threadId) {
        Dialog dialog = company.getDialogs().stream().filter(conv -> conv.getId().equals(dialogId)).findFirst().get();
        assertNotNull(dialog);
        assertFalse(dialog.getThreads().stream().anyMatch(aThread -> aThread.getId().equals(threadId)));
    }
}
