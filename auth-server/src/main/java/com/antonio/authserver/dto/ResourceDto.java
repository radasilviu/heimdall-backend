package com.antonio.authserver.dto;

import com.antonio.authserver.entity.Privilege;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
public class ResourceDto {

    private String name;

    public ResourceDto() {
    }

    public ResourceDto(String name) {
        this.name = name;
    }
}
