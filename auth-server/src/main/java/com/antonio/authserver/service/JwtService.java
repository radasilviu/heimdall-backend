package com.antonio.authserver.service;

import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.utils.SecurityConstants;
import io.jsonwebtoken.*;
import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;

@Service
public class JwtService {


    private Environment env;
    private UserService userService;


    public JwtService(Environment env, UserService userService) {
        this.env = env;
        this.userService = userService;
    }

    public String createAccessToken(String issuer, Date expirationTime, Collection<? extends GrantedAuthority> authorities, String secretKey) {

        final AppUserDto user = userService.getUserByUsername(issuer);

        String token = Jwts.builder()
                .setIssuer(issuer)
                .setSubject(user.toString())
                .claim("authorities", authorities)
                .setExpiration(expirationTime)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();

        return token;
    }


    public Claims decodeJWT(String jwt) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(SecurityConstants.TOKEN_SECRET)
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expired");
        } catch (JwtException ex) {
            throw new RuntimeException("Token can not be trusted");
        }

        return claims;
    }

}
