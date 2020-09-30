package com.antonio.authserver.model.oauth;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class OAuthSocialUser implements Serializable {

    private Long id;
    private String email;
    private String name;
    private String photoUrl;
    private String firstName;
    private String lastName;
    private String authToken;
    private String idToken;
    private String authorizationCode;
    private String provider;

}
