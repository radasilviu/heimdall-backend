package com.antonio.authserver.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    private String password;

    private String confirmPassword;

    private String email;

    private String forgotPasswordCode;
}
