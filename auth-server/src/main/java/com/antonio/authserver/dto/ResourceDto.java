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

    public ResourceDto() {
    }
    public ResourceDto(String name, Set<Privilege> privileges) {
        this.name = name;
        this.privileges = privileges;
;
    }
}
