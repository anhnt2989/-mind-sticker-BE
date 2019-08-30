package com.sm.ms.form.request;

import javax.validation.constraints.NotBlank;

import javax.validation.constraints.Size;

public class CreateNoteForm {
    @NotBlank
    @Size(min = 2, max = 50)
    private String title;

//    @NotBlank
    @Size(min = 4, max = 5000)
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
