package com.antonio.authserver.entity;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.engine.internal.Cascade;

@Entity
@Table(name = "app_user")
@Getter
@Setter
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String username;
    private String password;
    private String email;
    private Boolean isActivated = false;
    @Column(updatable = false)
    private String emailCode;

    @Column(columnDefinition = "TEXT")
    private String token;

    @Column(columnDefinition = "TEXT")
    private String refreshToken;

    @Column(columnDefinition = "TEXT")
    private String code;

    private String forgotPasswordCode;

    @ManyToMany
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

	@ManyToOne
	private Realm realm;

    @ManyToOne(targetEntity = IdentityProvider.class, fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    private IdentityProvider identityProvider;

    public AppUser() {

    }

    public AppUser(String username, String password, Set<Role> roles, String email, Boolean isActivated,
                   String emailCode) {
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.email = email;
        this.isActivated = isActivated;
        this.emailCode = emailCode;
    }

    public AppUser(String username, String password, Set<Role> roles, String email, Boolean isActivated,
                   String emailCode, Realm realm, IdentityProvider identityProvider) {
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.email = email;
        this.isActivated = isActivated;
        this.emailCode = emailCode;
        this.realm = realm;
        this.identityProvider = identityProvider;
    }
}
