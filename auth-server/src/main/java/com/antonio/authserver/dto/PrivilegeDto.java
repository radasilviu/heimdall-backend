package com.antonio.authserver.dto;
import com.antonio.authserver.entity.Resource;
import com.antonio.authserver.entity.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
public class PrivilegeDto {

    private String name;
    public PrivilegeDto() {
    }
    public PrivilegeDto(String name) {
        this.name = name;
    }
}
