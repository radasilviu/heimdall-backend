package com.antonio.authserver.dto;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class RealmDto {

    private String name;

    private String displayName;

    private boolean enabled;

    private boolean userRegistration;

    private boolean editUsername;

    private boolean forgotPassword;

    private boolean rememberMe;

    private boolean verifyEmail;

    private boolean loginWithEmail;

    public RealmDto() {
    }
    public RealmDto(String name, String displayName, boolean enabled, boolean userRegistration, boolean editUsername, boolean forgotPassword, boolean rememberMe, boolean verifyEmail, boolean loginWithEmail) {
        this.name = name;
        this.displayName = displayName;
        this.enabled = enabled;
        this.userRegistration = userRegistration;
        this.editUsername = editUsername;
        this.forgotPassword = forgotPassword;
        this.rememberMe = rememberMe;
        this.verifyEmail = verifyEmail;
        this.loginWithEmail = loginWithEmail;
    }
}
