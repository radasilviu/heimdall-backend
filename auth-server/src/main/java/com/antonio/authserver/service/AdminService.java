package com.antonio.authserver.service;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.model.AdminCredential;
import com.antonio.authserver.model.CustomException;
import com.antonio.authserver.model.JwtObject;
import com.antonio.authserver.utils.SecurityConstants;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class AdminService {

    private UserService userService;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private BCryptPasswordEncoder encoder;

    @Autowired
    public AdminService(UserService userService, AuthenticationManager authenticationManager, JwtService jwtService, BCryptPasswordEncoder encoder) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.encoder = encoder;
    }

    public JwtObject adminLogin(AdminCredential adminCredential) {
        final AppUserDto appUser = userService.getUserByUsernameAndRealmName(adminCredential.getRealm(),adminCredential.getUsername());
        verifyIfUserIsAuthorized(appUser);

        setAuthentication(adminCredential);

        final JwtObject jwtObject = createJWTObject(appUser);
        setJwtToUserAndSave(appUser, jwtObject.getAccess_token(), jwtObject.getRefresh_token());

        return jwtObject;
    }

    private void setJwtToUserAndSave(AppUserDto userDto, String token, String refreshToken) {
        userDto.setToken(token);
        userDto.setRefreshToken(refreshToken);
        userService.update(userDto);
    }

    private void verifyIfUserIsAuthorized(AppUserDto user) {

        boolean isAdmin = false;
        for (Role role : user.getRoles()) {
            if (role.getName().equals("ROLE_ADMIN")) {
                isAdmin = true;
                break;
            }
        }
        if (!isAdmin)
            throw new CustomException("User with the username [ " + user.getUsername() + " ] is not authorized!",
                    HttpStatus.UNAUTHORIZED);
    }

    private JwtObject createJWTObject(AppUserDto appUser) {
        final long accessTokenExpirationTime = System.currentTimeMillis() + SecurityConstants.TOKEN_EXPIRATION_TIME;
        final long refreshTokenExpirationTime = System.currentTimeMillis()
                + SecurityConstants.REFRESH_TOKEN_EXPIRATION_TIME;
        final String accessToken = jwtService.createAccessToken(appUser, accessTokenExpirationTime,
                getGrantedAuthoritySet(appUser.getRoles()), SecurityConstants.TOKEN_SECRET);
        final String refreshToken = jwtService.createRefreshToken(refreshTokenExpirationTime,
                SecurityConstants.TOKEN_SECRET);
        final JwtObject jwtObject = new JwtObject(appUser.getUsername(), accessToken, refreshToken,
                accessTokenExpirationTime, refreshTokenExpirationTime, appUser.getIdentityProvider().getProvider());

        return jwtObject;
    }

    private Set<GrantedAuthority> getGrantedAuthoritySet(Set<Role> authorities) {
        return authorities.stream().map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .collect(Collectors.toSet());
    }

    private Authentication setAuthentication(AdminCredential adminCredential) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                adminCredential.getUsername(), adminCredential.getPassword(), new ArrayList<>()));
        return authentication;
    }

    public void setCookie(HttpServletResponse response, String data) {

        Cookie cookie = new Cookie("user", data);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        response.addCookie(cookie);
        System.out.println(response.getHeader("Set-Cookie"));
    }


}
