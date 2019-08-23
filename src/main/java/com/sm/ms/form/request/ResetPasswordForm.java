package com.sm.ms.form.request;

import javax.validation.constraints.NotBlank;

import javax.validation.constraints.Size;

public class ResetPasswordForm {
    @NotBlank
    private String gmail;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}