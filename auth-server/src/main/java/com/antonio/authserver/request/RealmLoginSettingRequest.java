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
}
