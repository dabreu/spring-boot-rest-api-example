package com.dialogs.dto;

import com.dialogs.model.DialogThread;

/**
 * DTO containing information for a given dialog's thread
 */
public class TranscriptThread {

    private Integer order;
    private String text;

    public TranscriptThread() {
    }

    public TranscriptThread(DialogThread thread) {
        this.order = thread.getOrder() + 1;
        this.text = thread.getPayload();
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
