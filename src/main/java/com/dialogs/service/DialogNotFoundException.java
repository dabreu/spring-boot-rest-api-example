package com.dialogs.service;

public class DialogNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DialogNotFoundException(String message) {
        super(message);
    }
}