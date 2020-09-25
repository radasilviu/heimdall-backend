package com.antonio.authserver.entity;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

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
	private String code;

	@ManyToMany
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

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

}
