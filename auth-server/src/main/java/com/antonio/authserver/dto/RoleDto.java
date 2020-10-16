package com.antonio.authserver.dto;

import com.antonio.authserver.entity.Privilege;
import com.antonio.authserver.entity.Realm;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
public class RoleDto {

    private String name;
    private Realm realm;
    private Set<PrivilegeDto> privileges;


    public RoleDto() {
    }

    public RoleDto(String name,Realm realm,Set<PrivilegeDto> privileges) {
        this.name = name;
        this.realm = realm;
        this.privileges=privileges;
    }
}
