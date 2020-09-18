package com.antonio.authserver.service;

import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Code;
import com.antonio.authserver.model.JwtObject;
import com.antonio.authserver.model.LoginCredential;
import com.antonio.authserver.repository.AppUserRepository;
import com.antonio.authserver.repository.CodeRepository;
import com.antonio.authserver.request.ClientLoginRequest;
import com.antonio.authserver.utils.SecurityConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.*;

@Service
public class AuthService {
    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private CodeRepository codeRepository;

    @Autowired
    Environment env;

    private final long clientAuthCodeExpiration = 60000; // in milliseconds;

    private final AuthenticationManager authenticationManager;

    public AuthService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public Code getCode(ClientLoginRequest request) {
        Optional<AppUser> userOptional = appUserRepository.findByUsernameAndPassword(request.getUsername(), request.getPassword());

        if (!userOptional.isPresent()) {
            throw new RuntimeException("Bad credentials!");
        }

        final AppUser user = userOptional.get();
        Code code = createOauthCode(user);
        saveUserWithNewCodeValue(user, code);

        return code;
    }

    private void saveUserWithNewCodeValue(AppUser user, Code code) {
        user.setCode(code.getCode());
        appUserRepository.save(user);

    }

    private Code createOauthCode(AppUser user) {
        String jwtCode = generateCode(user);

        if (!jwtCode.equals("")) {
            final Code code = new Code(jwtCode);
            return code;
        }
        return null;
    }

    private String generateCode(AppUser user) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(this.env.getProperty("JWTSecretKey"));
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        long expireTime = nowMillis + clientAuthCodeExpiration;
        Date exp = new Date(expireTime);

        String jsonString = convertUserToJSON(user);

        JwtBuilder builder = Jwts.builder()
                .setIssuer(user.getUsername())
                .setSubject(jsonString)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(signatureAlgorithm, signingKey);

        return builder.compact();
    }

    private String convertUserToJSON(AppUser user) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";

        try {
            jsonString = mapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return jsonString;
    }

    public JwtObject login(LoginCredential loginCredential) {
        String code = loginCredential.getClientCode();
        Claims claims = decodeJWT(code);

        verifyClientCredential(code);
        Date expirationTime = new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME);

        final String token = createAccessToken(claims.getIssuer(), expirationTime);
        final JwtObject jwtObject = new JwtObject(expirationTime.getTime(), token);

        return jwtObject;
    }

    private void verifyClientCredential(String clientCode) {
        final Optional<AppUser> userOptional = appUserRepository.findByCode(clientCode);

        if (!userOptional.isPresent()) {
            throw new RuntimeException("Your client do not have permission to use this app");
        }

    }

    private Claims decodeJWT(String jwt) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(this.env.getProperty("JWTSecretKey")))
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch(ExpiredJwtException e) {
            throw new RuntimeException("Token expired");
        }

        return claims;
    }

    private String createAccessToken(String username, Date expirationTime) {
        String token = Jwts.builder()
                .setSubject(username)
                .setExpiration(expirationTime)
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.TOKEN_SECRET)
                .compact();

        return token;
    }
}
