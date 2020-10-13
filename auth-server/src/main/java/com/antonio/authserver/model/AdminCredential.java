package com.antonio.authserver.model;

public class AdminCredential {

    private String username;
    private String password;
    private String realm;
    public String getRealm() {
        return realm;
    }
    public void setRealm(String realm) {
        this.realm = realm;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
