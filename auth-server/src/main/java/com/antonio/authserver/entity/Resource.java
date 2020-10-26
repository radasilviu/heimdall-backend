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
    @ManyToMany(mappedBy = "roleResources")
    @JsonIgnore
    private Set<Role> roles;

    private String roleName;
    private String realmName;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "resource_privileges",
            joinColumns = @JoinColumn(
                    name = "resource_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "privilege_id", referencedColumnName = "id"))
    private Set<Privilege> privileges;

    public Resource() {
    }
    public Resource(String name, Set<Role> roles, String roleName, String realmName, Set<Privilege> privileges) {
        this.name = name;
        this.roles = roles;
        this.roleName = roleName;
        this.realmName = realmName;
        this.privileges = privileges;
    }
}
