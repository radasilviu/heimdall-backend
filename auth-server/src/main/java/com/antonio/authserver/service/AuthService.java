package com.antonio.authserver.service;

import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.dto.ClientDto;
import com.antonio.authserver.model.Code;
import com.antonio.authserver.model.JwtObject;
import com.antonio.authserver.model.LoginCredential;
import com.antonio.authserver.model.exceptions.controllerexceptions.CodeNotFound;
import com.antonio.authserver.model.exceptions.controllerexceptions.IncorrectPassword;
import com.antonio.authserver.model.exceptions.controllerexceptions.TokenNotFound;
import com.antonio.authserver.request.ClientLoginRequest;
import com.antonio.authserver.utils.SecurityConstants;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

        final ClientDto client = clientService.getClientByName(clientLoginRequest.getClientId());
        verifyClientCredential(clientLoginRequest.getClientSecret(), client.getClientSecret());

        final AppUserDto user = userService.findByUsernameAndPassword(clientLoginRequest.getUsername(), clientLoginRequest.getPassword());

        Code code = createOauthCode(user);
        saveUserWithNewCodeValue(user, code);

        return code;
    }

    private void verifyClientCredential(String currentPassword, String storedPassword) {

        if (!passwordEncoder.matches(currentPassword, storedPassword)) {
            throw new IncorrectPassword(currentPassword);
        }
    }


    private void saveUserWithNewCodeValue(AppUserDto user, Code code) {
        user.setCode(code.getCode());
        userService.update(user);

    }

    private Code createOauthCode(AppUserDto user) {
        String jwtCode = generateCode(user);

        if (!jwtCode.equals("")) {
            final Code code = new Code(jwtCode);
            return code;
        }
        return null;
    }

    private String generateCode(AppUserDto userDto) {

        long expirationTime = System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME;

        String token = jwtService.createAccessToken(userDto.getUsername(), expirationTime, new ArrayList<>(), SecurityConstants.TOKEN_SECRET);

        return token;
    }


    public JwtObject login(LoginCredential loginCredential) {
        String code = loginCredential.getClientCode();
        verifyClientCode(code);

        Claims claims = jwtService.decodeJWT(code);
        final AppUserDto user = userService.getUserByUsername(claims.getIssuer());


        long expirationTime = System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME;
        final String token = jwtService.createAccessToken(claims.getIssuer(), expirationTime, new ArrayList<>(), SecurityConstants.TOKEN_SECRET);
        final JwtObject jwtObject = new JwtObject(expirationTime, token);


        setJwtToUserAndSave(user, token);

        return jwtObject;
    }


    private void setJwtToUserAndSave(AppUserDto userDto, String token) {
        userDto.setToken(token);
        userService.update(userDto);
    }

    private void verifyClientCode(String clientCode) {
        userService.verifyUserCode(clientCode);
    }

    public void logout(String jwt) {
        final AppUserDto appUserDto = userService.findByToken(jwt);
        appUserDto.setToken(null);

        userService.update(appUserDto);

    }
}
