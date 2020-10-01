package com.antonio.authserver.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RealmDto {

    private String name;

    private String displayName;

    private boolean enabled;


    public RealmDto() {
    }

    public RealmDto(String name) {
        this.name = name;
    }
}
