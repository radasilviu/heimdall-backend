package com.antonio.authserver.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class IdentityProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String provider;

    @OneToMany(mappedBy = "identityProvider",  orphanRemoval = true)
    private List<AppUser> users = new ArrayList<>();

    public IdentityProvider() {
    }

    public IdentityProvider(String provider) {
        this.provider = provider;
    }
}
