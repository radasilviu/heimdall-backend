package com.antonio.authserver.entity;
import java.util.Set;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import lombok.Getter;
import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;
@Entity
@Getter
@Setter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Realm realm;

    @ManyToMany
    @JoinTable(
            name = "role_resources",
            joinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "resource_id", referencedColumnName = "id"))
    private Set<Resource> roleResources;

    public Role() {
    }

    public Role(String name, Realm realm,Set<Resource> resources) {
        this.name = name;
        this.realm = realm;
        this.roleResources = resources;
    }
}
