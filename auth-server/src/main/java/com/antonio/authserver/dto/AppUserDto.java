package com.antonio.authserver.dto;

import com.antonio.authserver.entity.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class AppUserDto {

    private Long id;
    private String username;
    private String password;
    private String token;
    private String refreshToken;
    private String code;
    private Set<Role> roles = new HashSet<>();


    @Override
    public String toString() {
        return "AppUserDto{" +
                "username='" + username + '\'' +
                ", roles=" + roles +
                '}';
    }

    public AppUserDto() {
    }

}
