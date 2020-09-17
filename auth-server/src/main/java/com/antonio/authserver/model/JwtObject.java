package com.antonio.authserver.model;

import java.io.Serializable;

public class JwtObject implements Serializable {
    private String access_token;
    private Long expireTime;

    public JwtObject(Long expireTime, String access_token) {
        this.expireTime = expireTime;
        this.access_token = access_token;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }
}
