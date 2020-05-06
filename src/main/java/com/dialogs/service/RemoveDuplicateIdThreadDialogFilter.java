package com.dialogs.service;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.dialogs.api.service.DataFilter;
import com.dialogs.model.Company;
import com.dialogs.model.Dialog;
import com.dialogs.model.DialogThread;
import com.dialogs.model.DialogThreadConflict;
import com.dialogs.repository.DialogThreadConflictRepository;

/**
 * This filter removes those threads of a dialog having duplicated Id. The
 * duplicated filtered threads are stored separately
 */
@Component
public class RemoveDuplicateIdThreadDialogFilter extends ThreadDialogFilter implements DataFilter {

    private final DialogThreadConflictRepository repository;

    public RemoveDuplicateIdThreadDialogFilter(DialogThreadConflictRepository repository) {
        this.repository = repository;
    }

    @Override
    public void apply(Company company) {
        filter(company);
    }

    @Override
    protected Predicate<DialogThread> getThreadFilteringPredicate(Dialog dialog) {
        Map<Long, Long> frequency = dialog.getThreads().stream().map(thread -> thread.getId())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        return thread -> frequency.get(thread.getId()) == 1;
    }

    @Override
    protected void handleNoMatchingThreads() {
        _nomatchingThreads.forEach(thread -> {
            DialogThreadConflict threadConflict = new DialogThreadConflict(thread);
            repository.save(threadConflict);
        });
    }
}
