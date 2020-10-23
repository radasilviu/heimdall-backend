package com.antonio.authserver.entity;
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
    @ManyToMany(mappedBy = "roleResources")
    private Set<Role> roles;

    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinTable(
            name = "resource_privileges",
            joinColumns = @JoinColumn(
                    name = "resource_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "privilege_id", referencedColumnName = "id"))
    private Set<Privilege> privileges;

    public Resource() {
    }
    public Resource(String name, Set<Role> roles,Set<Privilege> privileges) {
        this.name = name;
        this.roles = roles;
        this.privileges = privileges;
    }
}
