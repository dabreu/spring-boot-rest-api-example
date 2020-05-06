package com.dialogs.service;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.springframework.stereotype.Component;

import com.dialogs.api.service.DataFilter;
import com.dialogs.model.Company;
import com.dialogs.model.Dialog;
import com.dialogs.model.DialogThread;

/**
 * This filter removes those threads of a dialog having duplicated playload,
 * keeping only the most recent one. The filtered/removed threads are discarded
 * (not persisted)
 */
@Component
public class RemoveDuplicateTextThreadDialogFilter extends ThreadDialogFilter implements DataFilter {

    @Override
    public void apply(Company company) {
        filter(company);
    }

    @Override
    protected Predicate<DialogThread> getThreadFilteringPredicate(Dialog dialog) {
        Set<String> payloads = new HashSet<String>();
        return thread -> payloads.add(thread.getPayload());
    }
}