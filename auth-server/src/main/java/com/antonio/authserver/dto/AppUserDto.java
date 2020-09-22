package com.antonio.authserver.dto;

import com.antonio.authserver.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    public AppUserDto(String username, String password, String code, String token, Set<Role> roles) {
        this.username = username;
        this.password = password;
        this.code = code;
        this.token = token;
        this.roles = roles;
    }
}
