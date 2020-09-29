package com.antonio.authserver.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RealmLoginSettingRequest {
    private Long id;

    private boolean userRegistration;

    private boolean editUsername;

    private boolean forgotUsername;

    private boolean rememberMe;

    private boolean verifyEmail;

    private boolean loginWithEmail;
}
