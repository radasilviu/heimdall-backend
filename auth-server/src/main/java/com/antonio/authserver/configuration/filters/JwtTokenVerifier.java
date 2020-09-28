package com.antonio.authserver.configuration.filters;

import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.model.exceptions.controllerexceptions.TokenNotFound;
import com.antonio.authserver.service.JwtService;
import com.antonio.authserver.service.UserService;
import com.antonio.authserver.utils.SecurityConstants;
import com.google.common.base.Strings;
import io.jsonwebtoken.Claims;
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


        if (Strings.isNullOrEmpty(authorizationHeader)) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
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
        return authorizationHeader.replace(SecurityConstants.BEARER_TOKEN_PREFIX, "");
    }

    private AppUserDto extractUserFromToken(String token) {
        final Claims claims = jwtService.decodeJWT(token);
        final String username = claims.getIssuer();
        final AppUserDto user = userService.getUserByUsername(username);

        return user;
    }

    private void verifyToken(String currentToken, String userToken) {
        if (!currentToken.equals(userToken)) {
            throw new TokenNotFound(currentToken);
        }
    }

    private Set<GrantedAuthority> getGrantedAuthoritySet(Set<Role> authorities) {
        return authorities.stream().map(authority -> new SimpleGrantedAuthority(authority.getName())).collect(Collectors.toSet());
    }

    private void setAuthentication(String username, Set<GrantedAuthority> grantedAuthorities) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        boolean shouldNotFilter = false;

        if (request.getServletPath().startsWith("/oauth") || request.getServletPath().startsWith("/admin"))
            shouldNotFilter = true;

        if (request.getServletPath().endsWith("access"))
            shouldNotFilter = false;


        return shouldNotFilter;
    }
}
