package com.sm.ms.form.request;

import javax.validation.constraints.NotBlank;

import javax.validation.constraints.Size;

public class CreateNoteForm {
    @Size(max = 50)
    private String title;

    @Size(max = 5000)
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
