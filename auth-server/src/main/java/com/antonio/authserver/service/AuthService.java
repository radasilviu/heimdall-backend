package com.antonio.authserver.service;

import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.dto.ClientDto;
import com.antonio.authserver.model.Code;
import com.antonio.authserver.model.JwtObject;
import com.antonio.authserver.model.LoginCredential;
import com.antonio.authserver.model.exceptions.controllerexceptions.ClientNotFound;
import com.antonio.authserver.model.exceptions.controllerexceptions.IncorrectPassword;
import com.antonio.authserver.request.ClientLoginRequest;
import com.antonio.authserver.utils.SecurityConstants;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AuthService {

    private BCryptPasswordEncoder passwordEncoder;
    private ClientService clientService;
    private UserService userService;
    private JwtService jwtService;
    private Environment env;

    @Autowired
    public AuthService(BCryptPasswordEncoder passwordEncoder, ClientService clientService, UserService userService, JwtService jwtService, Environment env, AuthenticationManager authenticationManager) {
        this.passwordEncoder = passwordEncoder;
        this.clientService = clientService;
        this.userService = userService;
        this.jwtService = jwtService;
        this.env = env;
        this.authenticationManager = authenticationManager;
    }

    private final AuthenticationManager authenticationManager;


    public Code getCode(ClientLoginRequest clientLoginRequest) {

        verifyClientCredential(clientLoginRequest.getClientId(), clientLoginRequest.getClientSecret());

        final AppUserDto user = userService.findByUsernameAndPassword(clientLoginRequest.getUsername(), clientLoginRequest.getPassword());

        Code code = createOauthCode(user);
        saveUserWithNewCodeValue(user, code);

        return code;
    }

    private void verifyClientCredential(String clientName, String clientSecret) {
        final ClientDto client = clientService.getClientByName(clientName);

        if (!passwordEncoder.matches(clientSecret, client.getClientSecret())) {
            throw new IncorrectPassword(clientSecret);
        }
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

        verifyClientCode(code);
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

    private void verifyClientCode(String clientCode) {
        userService.findByCode(clientCode);
    }

    public void logout(String username) {
        final AppUserDto appUserDto = userService.findByUsername(username);
        appUserDto.setToken(null);

        userService.save(appUserDto);

    }
}
