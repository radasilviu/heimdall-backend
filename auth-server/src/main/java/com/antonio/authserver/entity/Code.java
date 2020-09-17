package com.antonio.authserver.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Code {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    private String code;


    public Code() {
    }

    public Code(String code) {
        this.code = code;
    }
}
