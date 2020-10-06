package com.antonio.authserver.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileLoginRequest {

    private String username;

    private String password;

    private String realm;
}
