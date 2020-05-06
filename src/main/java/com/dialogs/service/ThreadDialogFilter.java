package com.dialogs.service;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.dialogs.model.Company;
import com.dialogs.model.Dialog;
import com.dialogs.model.DialogThread;

/**
 * Abstract class for filtering dialog's threads of a company
 */
public abstract class ThreadDialogFilter {

    List<DialogThread> _matchingThreads;
    List<DialogThread> _nomatchingThreads;

    public void filter(Company company) {
        company.getDialogs().forEach(dialog -> filter(dialog));
    }

    private void filter(Dialog dialog) {
        Predicate<DialogThread> predicate = getThreadFilteringPredicate(dialog);
        filterThreads(dialog, predicate);
        dialog.setThreads(_matchingThreads);
        handleNoMatchingThreads();
    }

    protected abstract Predicate<DialogThread> getThreadFilteringPredicate(Dialog dialog);

    private void filterThreads(Dialog dialog, Predicate<DialogThread> predicate) {
        Map<Boolean, List<DialogThread>> partition = dialog.getThreads().stream().collect(Collectors.partitioningBy(predicate));
        _matchingThreads = partition.get(true);
        _nomatchingThreads = partition.get(false);
    }

    protected void handleNoMatchingThreads() {
        // by default, no need to handle no matching threads
    }
}
