package com.antonio.authserver.dto;
import java.util.HashSet;
import java.util.Set;

import com.antonio.authserver.entity.IdentityProvider;
import com.antonio.authserver.entity.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppUserDto {

	private Long id;
	private String username;
	private String password;
	private String token;
	private String refreshToken;
	private String code;
	private Set<Role> roles = new HashSet<>();
	private String email;
	private Boolean isActivated;
	private String emailCode;
	private IdentityProvider identityProvider;

	@Override
	public String toString() {
		return "AppUserDto{" + "username='" + username + '\'' + ", roles=" + roles + '}';
	}

	public AppUserDto() {
	}

	public AppUserDto(String username, String password, String code, String token, Set<Role> roles, String email,
			Boolean isActivated, String emailCode) {
		this.username = username;
		this.password = password;
		this.code = code;
		this.token = token;
		this.roles = roles;
		this.email = email;
		this.isActivated = isActivated;
		this.emailCode = emailCode;
	}
}
