package com.antonio.authserver.service;

import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.model.AdminCredential;
import com.antonio.authserver.model.JwtObject;
import com.antonio.authserver.repository.AppUserRepository;
import com.antonio.authserver.utils.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private AppUserRepository appUserRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;

    @Autowired
    public AdminService(AppUserRepository appUserRepository, BCryptPasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public JwtObject validateAdminService(AdminCredential adminCredential) {

        final Optional<AppUser> userOptional = appUserRepository.findByUsername(adminCredential.getUsername());

        if (!userOptional.isPresent()) {
            throw new UsernameNotFoundException("Invalid Credentials! User with username: " + adminCredential.getUsername() + " doesn't exist");
        }

        final AppUser user = userOptional.get();

        if (!passwordEncoder.matches(adminCredential.getPassword(), user.getPassword())) {
            throw new UsernameNotFoundException("Password is not correct!");
        }


        Authentication authentication = getAuthentication(adminCredential);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        final long expirationTime = System.currentTimeMillis() / 1000 + SecurityConstants.EXPIRATION_TIME;
        Date expDate = new Date(expirationTime);
        final String accessToken = jwtService.createAccessToken(user.getUsername(), expDate, authentication.getAuthorities(), SecurityConstants.TOKEN_SECRET);

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

    private static List<GrantedAuthority> getAuthorities(Set<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }

}
