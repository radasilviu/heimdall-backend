package com.antonio.authserver.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class JwtObject implements Serializable {
    private String username;
    private String access_token;
    private String refresh_token;
    private Long token_expire_time;
    private Long refresh_token_expire_time;

    public JwtObject() {
    }


    public JwtObject(String username, String access_token, String refresh_token, Long token_expire_time, Long refresh_token_expire_time) {
        this.username = username;
        this.access_token = access_token;
        this.refresh_token = refresh_token;
        this.token_expire_time = token_expire_time;
        this.refresh_token_expire_time = refresh_token_expire_time;
    }
}
