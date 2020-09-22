package com.antonio.authserver.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDto {

    private String name;


    public RoleDto() {
    }

    public RoleDto(String name) {
        this.name = name;
    }
}
