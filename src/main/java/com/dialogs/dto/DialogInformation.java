package com.dialogs.dto;

/**
 * DTO containing dialog transcript information
 */
import java.util.List;
import java.util.stream.Collectors;

import com.dialogs.model.Dialog;
import com.dialogs.model.DialogThread;

public class DialogInformation {

    private Long id;

    private List<TranscriptThread> transcript;

    public DialogInformation() {
    }

    public DialogInformation(Dialog dialog) {
        this.id = dialog.getId();
        this.transcript = generateTranscript(dialog.getThreads());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<TranscriptThread> getTranscript() {
        return transcript;
    }

    public void setTranscript(List<TranscriptThread> transcript) {
        this.transcript = transcript;
    }

    private List<TranscriptThread> generateTranscript(List<DialogThread> threads) {
        return threads.stream().map(thread -> new TranscriptThread(thread)).collect(Collectors.toList());
    }
}
