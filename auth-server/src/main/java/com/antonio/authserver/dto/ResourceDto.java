package com.antonio.authserver.dto;
import com.antonio.authserver.entity.Privilege;
import com.antonio.authserver.entity.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
public class ResourceDto {

    private String name;
    private Set<Privilege> privileges;
    private String roleName;

    public ResourceDto() {
    }
    public ResourceDto(String name, Set<Privilege> privileges,String roleName) {
        this.name = name;
        this.privileges = privileges;
        this.roleName=roleName;
    }
}
