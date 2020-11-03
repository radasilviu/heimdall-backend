package com.antonio.authserver.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;
@Entity
@Table(name = "Resource")
@Getter
@Setter
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    public Resource() {
    }
    public Resource(String name) {
        this.name = name;
    }
}
