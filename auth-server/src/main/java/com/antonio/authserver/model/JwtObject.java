package com.antonio.authserver.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class JwtObject implements Serializable {
    private String access_token;
    private String refresh_token;
    private Long expireTime;

    public JwtObject() {
    }

    public JwtObject(Long expireTime, String access_token) {
        this.access_token = access_token;
        this.expireTime = expireTime;
    }

    public JwtObject(Long expireTime, String access_token, String refresh_token) {
        this.expireTime = expireTime;
        this.access_token = access_token;
        this.refresh_token = refresh_token;
    }


}
