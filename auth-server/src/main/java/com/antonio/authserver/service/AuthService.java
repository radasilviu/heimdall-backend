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
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private PasswordEncoder passwordEncoder;

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

        if (jwtCode != "") {
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

        //  verifyClientCredential(loginCredential.getClientCode());

        final Optional<AppUser> userOptional = appUserRepository.findByUsername(loginCredential.getUsername());
        verifyUserCredentials(userOptional, loginCredential);

        final Authentication authentication = authenticateUser(loginCredential);
        setCurrentUserToSecurityContext(authentication);


        final String token = createUserNameAndPasswordTokenAuth(authentication.getName(), authentication.getAuthorities());
        final Long expireTime = System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME;
        final JwtObject jwtObject = new JwtObject(expireTime, token);

        return jwtObject;
    }


    private void verifyClientCredential(String clientCode) {
        final Optional<Code> codeOptional = codeRepository.findByCode(clientCode);

        if (!codeOptional.isPresent()) {
            throw new RuntimeException("Your client do not have permission to use this app");
        }

    }

    private void setCurrentUserToSecurityContext(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Authentication authenticateUser(LoginCredential loginCredential) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginCredential.getUsername(),
                        loginCredential.getPassword(), new ArrayList<>()));

        return authentication;
    }

    private void verifyUserCredentials(Optional<AppUser> userOptional, LoginCredential loginCredential) {

        if (!userOptional.isPresent()) {
            throw new UsernameNotFoundException("User with username: " + loginCredential.getUsername() + " doesn't exist");
        }

        final AppUser user = userOptional.get();

        if (!loginCredential.getClientCode().equals(user.getCode())) {
            throw new RuntimeException("Client code is wrong");
        }

        if (!passwordEncoder.matches(loginCredential.getPassword(), user.getPassword())) {
            throw new UsernameNotFoundException("Password is not correct!");
        }

    }

    private String createUserNameAndPasswordTokenAuth(String username, Collection<? extends GrantedAuthority> authorities) {
        String token = Jwts.builder()
                .setSubject(username)
                .claim("authorities", authorities)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.TOKEN_SECRET)
                .compact();

        return token;

    }


}
