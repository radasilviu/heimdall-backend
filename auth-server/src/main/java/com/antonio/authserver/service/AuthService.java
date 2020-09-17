package com.antonio.authserver.service;

import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Code;
import com.antonio.authserver.model.JwtObject;
import com.antonio.authserver.model.LoginCredential;
import com.antonio.authserver.repository.AppUserRepository;
import com.antonio.authserver.repository.CodeRepository;
import com.antonio.authserver.request.ClientLoginRequest;
import com.antonio.authserver.utils.SecurityConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthService {
    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private CodeRepository codeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public Code getCode(ClientLoginRequest request) {
        Optional<AppUser> userOptional = appUserRepository.findByUsernameAndPassword(request.getUsername(), request.getPassword());

        if (!userOptional.isPresent()) {
            throw new RuntimeException("Bad credentials!");
        }

        Code code = createOauthCode();
        final AppUser user = userOptional.get();
        saveUserWithNewCodeValue(user, code);

        return code;
    }

    private void saveUserWithNewCodeValue(AppUser user, Code code) {
        user.setCode(code.getCode());
        appUserRepository.save(user);

    }

    private Code createOauthCode() {
        final Code code = new Code(generateCode());

        return code;
    }

    private String generateCode() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 6;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
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
