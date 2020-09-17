package com.antonio.authserver.model;

import java.io.Serializable;

public class LoginCredential implements Serializable {

    private String clientCode;

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

}
