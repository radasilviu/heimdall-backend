package com.antonio.authserver.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RealmLoginSettingRequest {
    private Long id;

    private String name;

    private boolean userRegistration;

    private boolean editUsername;

    private boolean forgotPassword;

    private boolean rememberMe;

    private boolean verifyEmail;

    private boolean loginWithEmail;
    public RealmLoginSettingRequest() {
    }
    public RealmLoginSettingRequest(String name, boolean userRegistration, boolean editUsername, boolean forgotPassword, boolean rememberMe, boolean verifyEmail, boolean loginWithEmail) {
        this.name = name;
        this.userRegistration = userRegistration;
        this.editUsername = editUsername;
        this.forgotPassword = forgotPassword;
        this.rememberMe = rememberMe;
        this.verifyEmail = verifyEmail;
        this.loginWithEmail = loginWithEmail;
    }
}
