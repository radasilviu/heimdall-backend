package com.antonio.authserver.configuration.filters;

import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.entity.Privilege;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.model.CustomException;
import com.antonio.authserver.service.JwtService;
import com.antonio.authserver.service.UserService;
import com.antonio.authserver.utils.SecurityConstants;
import com.google.common.base.Strings;
import io.jsonwebtoken.Claims;
import lombok.var;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtTokenVerifier extends OncePerRequestFilter {


    private final JwtService jwtService;
    private final UserService userService;

    public JwtTokenVerifier(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = httpServletRequest.getHeader(SecurityConstants.HEADER_AUTHORIZATION);


        String header = httpServletRequest.getHeader(SecurityConstants.RESOURCE);
        String request = httpServletRequest.getHeader(SecurityConstants.REQUEST);

        System.out.println(header);
        System.out.println(request);



        if (Strings.isNullOrEmpty(authorizationHeader)) {
            throw new CustomException("The user is not authorized to do this.", HttpStatus.UNAUTHORIZED);
        }

        if (authorizationHeader.startsWith(SecurityConstants.BEARER_TOKEN_PREFIX)) {

            final String token = extractToken(authorizationHeader, SecurityConstants.BEARER_TOKEN_PREFIX);
            final AppUserDto user = extractUserFromToken(token);

            verifyToken(token, user.getToken());
        }

        if (authorizationHeader.startsWith(SecurityConstants.BASIC_TOKEN_PREFIX)) {

            final String token = authorizationHeader.replace(SecurityConstants.BASIC_TOKEN_PREFIX, "");


            final AppUserDto user = extractUserFromToken(token);
            verifyToken(token, user.getToken());

            final Set<Role> authorities = user.getRoles();
            final Set<GrantedAuthority> grantedAuthorities = getGrantedAuthoritySet(authorities);
            setAuthentication(user.getUsername(), grantedAuthorities);

        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private String extractToken(String authorizationHeader, String key) {
        return authorizationHeader.replace(key, "");
    }

    private AppUserDto extractUserFromToken(String token) {
        final Claims claims = jwtService.decodeJWT(token);
        final String username = claims.getIssuer();
        final AppUserDto user = userService.findByUsername(username);

        return user;
    }

    private void verifyToken(String currentToken, String userToken) {
        if (!currentToken.equals(userToken)) {
            throw new CustomException("The token " + currentToken + " could not be found!", HttpStatus.NOT_FOUND);
        }
    }

    private Set<GrantedAuthority> getGrantedAuthoritySet(Set<Role> authorities) {
        Set<GrantedAuthority> simpleGrantedAuthorities = new HashSet<>();
        for(Role role : authorities){
            simpleGrantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
            for(Privilege privilege : role.getPrivileges()){
                simpleGrantedAuthorities.add(new SimpleGrantedAuthority(privilege.getName()));
            }
        }
        return simpleGrantedAuthorities;
    }

    private void setAuthentication(String username, Set<GrantedAuthority> grantedAuthorities) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        boolean shouldNotFilter = false;

        if (request.getServletPath().startsWith("/oauth") ||
            request.getServletPath().startsWith("/admin") ||
            request.getServletPath().contains("/check")
        )
            shouldNotFilter = true;

        if (request.getServletPath().endsWith("access"))
            shouldNotFilter = false;


        return shouldNotFilter;
    }
}
