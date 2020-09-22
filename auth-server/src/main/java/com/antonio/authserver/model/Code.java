package com.antonio.authserver.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Code implements Serializable {
    private String code;

    public Code() {
    }

    public Code(String code) {
        this.code = code;
    }

}
