package com.antonio.authserver.dto;

import com.antonio.authserver.entity.Realm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDto {

    private String name;
    private Realm realm;


    public RoleDto() {
    }

    public RoleDto(String name,Realm realm) {
        this.name = name;
        this.realm = realm;
    }
}
