package com.antonio.authserver.dto;

import com.antonio.authserver.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppUserDto {

    private String username;
    private String password;
    private String token;
    private String code;
    private Set<Role> roles;

    @Override
    public String toString() {
        return "AppUserDto{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public AppUserDto(String username, String password, String token, Set<Role> roles) {
        this.username = username;
        this.password = password;
        this.token = token;
        this.roles = roles;
    }
}
