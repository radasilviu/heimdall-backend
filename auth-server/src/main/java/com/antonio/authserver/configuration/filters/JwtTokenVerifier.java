package com.antonio.authserver.configuration.filters;

import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.model.CustomException;
import com.antonio.authserver.service.JwtService;
import com.antonio.authserver.service.PrivilegeService;
import com.antonio.authserver.service.RoleService;
import com.antonio.authserver.service.UserService;
import com.antonio.authserver.utils.SecurityConstants;
import com.google.common.base.Strings;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
public class JwtTokenVerifier extends OncePerRequestFilter {


    private final JwtService jwtService;
    private final UserService userService;
    private final PrivilegeService  privilegeService;
    private final RoleService roleService;
    @Autowired
    public JwtTokenVerifier(JwtService jwtService, UserService userService,PrivilegeService privilegeService,RoleService roleService) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.privilegeService = privilegeService;
        this.roleService = roleService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = httpServletRequest.getHeader(SecurityConstants.HEADER_AUTHORIZATION);
        if (Strings.isNullOrEmpty(authorizationHeader)) {
            throw new CustomException("The user is not authorized to do this.", HttpStatus.UNAUTHORIZED);
        }

        if (authorizationHeader.startsWith(SecurityConstants.BEARER_TOKEN_PREFIX)) {

            final String token = extractToken(authorizationHeader, SecurityConstants.BEARER_TOKEN_PREFIX);
            final AppUserDto user = extractUserFromToken(token);

            verifyToken(token, user.getToken());

            if(!roleService.checkFfUserIsAdmin(user)) {
                String resourceHeader = httpServletRequest.getHeader(SecurityConstants.RESOURCE);
                resourceHeader = resourceHeader.toUpperCase();
                String requestTypeHeader = httpServletRequest.getHeader(SecurityConstants.REQUEST);
                requestTypeHeader = requestTypeHeader.toUpperCase();
                if (!resourceHeader.equals("PUBLIC"))
                    checkIfUserHasNecessaryAuthorities(user, resourceHeader, requestTypeHeader);
            }
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
        return userService.findByUsername(username);
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

    @Transactional
    public void checkIfUserHasNecessaryAuthorities(AppUserDto user,String resourceHeader, String requestTypeHeader) {
        boolean hasPrivilegeForResource = false;
        for (Role role : user.getRoles()) {
            if (role.getName().equals("ROLE_ADMIN"))
                return;
            else {
                    if (!privilegeService.getResourceFromRole(role, resourceHeader).getName().equals("")) {
                        if(privilegeService.checkIfUserHasPrivilegeForResource(role, resourceHeader, requestTypeHeader))
                            hasPrivilegeForResource = true;
                    }
                }
            }
        if(!hasPrivilegeForResource)
            throw new CustomException("The user does not have the necessary authorities!",HttpStatus.BAD_REQUEST);
    }
}
