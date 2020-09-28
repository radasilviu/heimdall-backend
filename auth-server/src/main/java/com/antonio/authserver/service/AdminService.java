package com.antonio.authserver.service;

import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.model.AdminCredential;
import com.antonio.authserver.model.JwtObject;
import com.antonio.authserver.model.exceptions.controllerexceptions.UserNotAuthorized;
import com.antonio.authserver.model.exceptions.controllerexceptions.UserNotFound;
import com.antonio.authserver.repository.AppUserRepository;
import com.antonio.authserver.utils.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private AppUserRepository appUserRepository;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;

    @Autowired
    public AdminService(AppUserRepository appUserRepository, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.appUserRepository = appUserRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public JwtObject adminLogin(AdminCredential adminCredential) {

        final AppUser appUser = appUserRepository.findByUsername(adminCredential.getUsername()).orElseThrow(() -> new UserNotFound(adminCredential.getUsername()));
        verifyIfUserIsAuthorized(appUser);

        setAuthentication(adminCredential);

        final JwtObject jwtObject = createJWTObject(appUser);

        return jwtObject;
    }

    private void verifyIfUserIsAuthorized(AppUser user) {

        boolean isAdmin = false;
        for (Role role : user.getRoles()) {
            if (role.getName().equals("ROLE_ADMIN")) {
                isAdmin = true;
                break;
            }
        }
        if (!isAdmin)
            throw new UserNotAuthorized(user.getUsername());
    }

    private JwtObject createJWTObject(AppUser appUser) {
        final long expirationTime = System.currentTimeMillis() + SecurityConstants.TOKEN_EXPIRATION_TIME;
        final String accessToken = jwtService.createAccessToken(appUser.getUsername(), expirationTime, getGrantedAuthoritySet(appUser.getRoles()), SecurityConstants.TOKEN_SECRET);
        final JwtObject jwtObject = new JwtObject(expirationTime, accessToken);

        return jwtObject;
    }

    private Set<GrantedAuthority> getGrantedAuthoritySet(Set<Role> authorities) {
        return authorities.stream().map(authority -> new SimpleGrantedAuthority(authority.getName())).collect(Collectors.toSet());
    }


    private Authentication setAuthentication(AdminCredential adminCredential) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        adminCredential.getUsername(),
                        adminCredential.getPassword(), new ArrayList<>()));
        return authentication;
    }

}
