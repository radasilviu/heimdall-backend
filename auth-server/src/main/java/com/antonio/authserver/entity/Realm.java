package com.antonio.authserver.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Realm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String displayName;

    private boolean enabled;

    private boolean userRegistration;

    private boolean editUsername;

    private boolean forgotPassword;

    private boolean rememberMe;

    private boolean verifyEmail;

    private boolean loginWithEmail;
    public Realm() {
    }
    public Realm(String name, String displayName, boolean enabled, boolean userRegistration, boolean editUsername, boolean forgotPassword, boolean rememberMe, boolean verifyEmail, boolean loginWithEmail) {
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
