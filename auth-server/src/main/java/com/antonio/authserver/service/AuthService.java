package com.antonio.authserver.service;

import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.entity.Code;
import com.antonio.authserver.model.JwtObject;
import com.antonio.authserver.model.LoginCredential;
import com.antonio.authserver.repository.CodeRepository;
import com.antonio.authserver.request.ClientLoginRequest;
import com.antonio.authserver.utils.SecurityConstants;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AuthService {
    @Autowired
    private UserService userService;

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
        AppUserDto user = userService.findByUsernameAndPassword(request.getUsername(), request.getPassword());

        Code code = createOauthCode(user);
        saveUserWithNewCodeValue(user, code);

        return code;
    }

    private void saveUserWithNewCodeValue(AppUserDto user, Code code) {
        user.setCode(code.getCode());
        userService.save(user);

    }

    private Code createOauthCode(AppUserDto user) {
        String jwtCode = generateCode(user);

        if (!jwtCode.equals("")) {
            final Code code = new Code(jwtCode);
            return code;
        }
        return null;
    }

    private String generateCode(AppUserDto user) {

        long expirationTime = System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME;

        String token = jwtService.createAccessToken(user.getUsername(), expirationTime, new ArrayList<>(), SecurityConstants.TOKEN_SECRET);

        return token;
    }


    public JwtObject login(LoginCredential loginCredential) {
        String code = loginCredential.getClientCode();
        Claims claims = jwtService.decodeJWT(code);

        verifyClientCredential(code);
        long expirationTime = System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME;


        final String token = jwtService.createAccessToken(claims.getIssuer(), expirationTime, new ArrayList<>(), SecurityConstants.TOKEN_SECRET);
        final JwtObject jwtObject = new JwtObject(expirationTime, token);

        final AppUserDto user = userService.getUserByUsername(claims.getIssuer());
        setJwtToUserAndSave(user, token);

        return jwtObject;
    }

    private void setJwtToUserAndSave(AppUserDto userDto, String token) {
        userDto.setToken(token);
        userService.save(userDto);
    }

    private void verifyClientCredential(String clientCode) {
        userService.findByCode(clientCode);
    }

    public void logout(String username) {
        final AppUserDto appUserDto = userService.findByUsername(username);
        appUserDto.setToken(null);

        userService.save(appUserDto);

    }
}
