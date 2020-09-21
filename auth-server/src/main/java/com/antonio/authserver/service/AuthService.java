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
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private CodeRepository codeRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    Environment env;


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

        long expirationTime = System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME;
        Date expDate = new Date(expirationTime);


        String token = jwtService.createAccessToken(user.getUsername(), expDate, new ArrayList<>(), SecurityConstants.TOKEN_SECRET);

        return token;
    }


    public JwtObject login(LoginCredential loginCredential) {
        String code = loginCredential.getClientCode();
        Claims claims = jwtService.decodeJWT(code);

        verifyClientCredential(code);
        long expirationTime = System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME;
        Date expDate = new Date(expirationTime);

        final String token = jwtService.createAccessToken(claims.getIssuer(), expDate, new ArrayList<>(), SecurityConstants.TOKEN_SECRET);
        final JwtObject jwtObject = new JwtObject(expirationTime, token);

        return jwtObject;
    }

    private void verifyClientCredential(String clientCode) {
        final Optional<AppUser> userOptional = appUserRepository.findByCode(clientCode);

        if (!userOptional.isPresent()) {
            throw new RuntimeException("Your client do not have permission to use this app");
        }

    }
}
