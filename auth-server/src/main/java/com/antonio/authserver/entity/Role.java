package com.antonio.authserver.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
@Data
public class Role {

    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
}
