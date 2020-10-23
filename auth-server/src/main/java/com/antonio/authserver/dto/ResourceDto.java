package com.antonio.authserver.dto;
import com.antonio.authserver.entity.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceDto {

    private String name;
    private Role role;

    public ResourceDto() {
    }
    public ResourceDto(String name, Role role) {
        this.name = name;
        this.role = role;
    }
}
