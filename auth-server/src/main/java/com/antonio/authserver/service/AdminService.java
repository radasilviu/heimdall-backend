package com.antonio.authserver.service;

import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.model.AdminCredential;
import com.antonio.authserver.model.JwtObject;
import com.antonio.authserver.model.exceptions.controllerexceptions.UserNotFound;
import com.antonio.authserver.repository.AppUserRepository;
import com.antonio.authserver.utils.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

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

    public JwtObject validateAdminService(AdminCredential adminCredential) {

        AppUser appUser = appUserRepository.findByUsername(adminCredential.getUsername()).orElseThrow(() -> new UserNotFound(adminCredential.getUsername()));

        Authentication authentication = getAuthentication(adminCredential);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        final long expirationTime = System.currentTimeMillis() + SecurityConstants.TOKEN_EXPIRATION_TIME;
        final String accessToken = jwtService.createAccessToken(appUser.getUsername(), expirationTime, authentication.getAuthorities(), SecurityConstants.TOKEN_SECRET);

        final JwtObject jwtObject = new JwtObject(expirationTime, accessToken);


        return jwtObject;
    }


    private Authentication getAuthentication(AdminCredential adminCredential) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        adminCredential.getUsername(),
                        adminCredential.getPassword(), new ArrayList<>()));
        return authentication;
    }

}
