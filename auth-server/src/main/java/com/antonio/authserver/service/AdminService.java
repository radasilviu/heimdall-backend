package com.antonio.authserver.service;

import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.model.AdminCredential;
import com.antonio.authserver.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private AppUserRepository appUserRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;

    @Autowired
    public AdminService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public void validateAdminService(AdminCredential adminCredential) {

        final Optional<AppUser> userOptional = appUserRepository.findByUsername(adminCredential.getUsername());

        if (!userOptional.isPresent()) {
            throw new UsernameNotFoundException("Invalid Credentials! User with username: " + adminCredential.getUsername() + " doesn't exist");
        }

        final AppUser user = userOptional.get();

        if (passwordEncoder.matches(adminCredential.getPassword(), user.getPassword())) {
            throw new UsernameNotFoundException("Invalid Credentials! Password Wrong");
        }


        Authentication authentication = getAuthentication(adminCredential);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Authentication getAuthentication(AdminCredential adminCredential) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        adminCredential.getUsername(),
                        adminCredential.getPassword(), new ArrayList<>()));
        return authentication;
    }

    private static List<GrantedAuthority> getAuthorities(Set<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }

}
