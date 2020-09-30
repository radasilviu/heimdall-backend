package com.antonio.authserver.service;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.model.CustomException;
import com.antonio.authserver.repository.AppUserRepository;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private AppUserRepository appUserRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws CustomException {
		final Optional<AppUser> userOptional = appUserRepository.findByUsername(username);

		if (!userOptional.isPresent()) {
			throw new CustomException("User with username: " + username + " doesn't exist", HttpStatus.NOT_FOUND);
		}

		final AppUser user = userOptional.get();

		return new org.springframework.security.core.userdetails.User(username, user.getPassword(),
				getAuthorities(user.getRoles()));
	}

	private static List<GrantedAuthority> getAuthorities(Set<Role> roles) {
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
	}
}
