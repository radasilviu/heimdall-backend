package com.antonio.authserver.configuration.filters;

import com.antonio.authserver.utils.SecurityConstants;
import com.google.common.base.Strings;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtTokenVerifier extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = httpServletRequest.getHeader(SecurityConstants.HEADER_AUTHORIZATION);


        if (Strings.isNullOrEmpty(authorizationHeader) || !authorizationHeader.startsWith(SecurityConstants.BEARER_TOKEN_PREFIX)) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

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

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        boolean shouldNotFilter = false;

        final String path = "/oauth";
        if (request.getServletPath().startsWith(path))
            shouldNotFilter = true;

        if (request.getServletPath().endsWith("access"))
            shouldNotFilter = false;


        return shouldNotFilter;
    }
}
