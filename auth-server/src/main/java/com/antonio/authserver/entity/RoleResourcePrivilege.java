package com.antonio.authserver.entity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Set;
@Entity
@Getter
@Setter
public class RoleResourcePrivilege {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Role role;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Resource resource;
    @ManyToMany
    @JoinTable(
            name = "resource_privileges",
            joinColumns = @JoinColumn(
                    name = "roleResourcePrivilege_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "privilege_id", referencedColumnName = "id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Privilege> privileges;

    public RoleResourcePrivilege() {
    }
    public RoleResourcePrivilege(Role role, Resource resource, Set<Privilege> privileges) {
        this.role = role;
        this.resource = resource;
        this.privileges = privileges;
    }
}
