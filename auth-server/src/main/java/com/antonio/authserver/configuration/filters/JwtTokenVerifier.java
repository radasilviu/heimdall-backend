package com.antonio.authserver.configuration.filters;

import com.antonio.authserver.utils.SecurityConstants;
import com.google.common.base.Strings;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtTokenVerifier extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = httpServletRequest.getHeader(SecurityConstants.HEADER_AUTHORIZATION);


        if (Strings.isNullOrEmpty(authorizationHeader)) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        if (authorizationHeader.startsWith(SecurityConstants.BEARER_TOKEN_PREFIX)) {


            String token = authorizationHeader.replace(SecurityConstants.BEARER_TOKEN_PREFIX, "");
            try {

                Jws<Claims> claimsJws = Jwts.parser()
                        .setSigningKey(SecurityConstants.TOKEN_SECRET)
                        .parseClaimsJws(token);
                Claims body = claimsJws.getBody();
                String username = body.getSubject();
            } catch (
                    JwtException e) {
                throw new IllegalStateException("Token could not be trusted: " + token);
            }
        }

        if (authorizationHeader.startsWith(SecurityConstants.BASIC_TOKEN_PREFIX)) {

            String token = authorizationHeader.replace(SecurityConstants.BASIC_TOKEN_PREFIX, "");

            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(SecurityConstants.TOKEN_SECRET)
                    .parseClaimsJws(token);
            Claims body = claimsJws.getBody();
            String username = body.getSubject();


            final List<Map<String, String>> authorities = (List<Map<String, String>>) body.get("authorities");
            final Set<GrantedAuthority> grantedAuthorities = authorities.stream().map(authority -> new SimpleGrantedAuthority(authority.get("authority"))).collect(Collectors.toSet());
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        boolean shouldNotFilter = false;

        final String path = "/oauth";
        if (request.getServletPath().startsWith(path) || request.getServletPath().startsWith("/admin/login"))
            shouldNotFilter = true;

        if (request.getServletPath().endsWith("access"))
            shouldNotFilter = false;


        return shouldNotFilter;
    }
}
