package com.antonio.authserver.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientLoginRequest {
    private String username;
    private String password;
    private String clientId;
    private String clientSecret;
    private String realm;
}
