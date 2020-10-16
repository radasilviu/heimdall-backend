package com.antonio.authserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;
@Entity
@Table(name = "Privileges")
@Getter
@Setter
public class Privilege {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "privileges")
    @JsonIgnore
    private Set<Role> roles;

    public Privilege() {
    }
    public Privilege(String name, Set<Role> roles) {
        this.name = name;
        this.roles = roles;
    }
}
