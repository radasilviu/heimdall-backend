package com.antonio.authserver.service.providers;


import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.repository.AppUserRepository;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
public class DefaultAuthenticationProvider implements AuthenticationProvider {

    public DefaultAuthenticationProvider(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    private AppUserRepository appUserRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Optional<AppUser> appUser = appUserRepository.findByUsername(authentication.getName());
        if (appUser.isPresent()) {
            AppUser user = appUser.get();
            String username = authentication.getName();
            String password = (String) authentication.getCredentials();
            if (username.equalsIgnoreCase(user.getUsername()) &&
                    password.equalsIgnoreCase(user.getPassword())) {
                return new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword(),
                        getAuthorities(user.getRoles())
                );
            }
        }

        throw new UsernameNotFoundException("User not found");
    }

    private static List<GrantedAuthority> getAuthorities(Set<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
