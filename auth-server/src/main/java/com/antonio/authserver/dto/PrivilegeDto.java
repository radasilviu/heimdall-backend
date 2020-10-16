package com.antonio.authserver.dto;
import com.antonio.authserver.entity.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
public class PrivilegeDto {

    private String name;
    private Set<Role> roles;
    public PrivilegeDto() {
    }
    public PrivilegeDto(String name, Set<Role> roles) {
        this.name = name;
        this.roles = roles;
    }
}
