package com.antonio.authserver.service;

import java.util.*;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.dto.ClientDto;
import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.model.Code;
import com.antonio.authserver.model.CustomException;
import com.antonio.authserver.model.JwtObject;
import com.antonio.authserver.model.LoginCredential;
import com.antonio.authserver.repository.AppUserRepository;
import com.antonio.authserver.request.ClientLoginRequest;
import com.antonio.authserver.utils.SecurityConstants;
import io.jsonwebtoken.Claims;

@Service
public class AuthService {

    private BCryptPasswordEncoder passwordEncoder;

    private ClientService clientService;

    private UserService userService;

    private JwtService jwtService;

    private Environment env;

    private AppUserRepository appUserRepository;

    private EmailService emailService;

    @Autowired
    public AuthService(BCryptPasswordEncoder passwordEncoder, ClientService clientService, UserService userService,
                       JwtService jwtService, Environment env, AuthenticationManager authenticationManager,
                       AppUserRepository appUserRepository, EmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.clientService = clientService;
        this.userService = userService;
        this.jwtService = jwtService;
        this.env = env;
        this.authenticationManager = authenticationManager;
        this.appUserRepository = appUserRepository;
        this.emailService = emailService;
    }

    private final AuthenticationManager authenticationManager;

    public Code getCode(ClientLoginRequest clientLoginRequest) {
        clientService.validateClient(clientLoginRequest.getClientId(), clientLoginRequest.getClientSecret());
        final AppUserDto user = userService.findByUsernameAndPassword(clientLoginRequest.getUsername(),
                clientLoginRequest.getPassword());


        Code code = clientService.generateCode(user);
        saveUserWithNewCodeValue(user, code);

        return code;
    }

    private void saveUserWithNewCodeValue(AppUserDto user, Code code) {
        user.setCode(code.getCode());
        userService.update(user);

    }

    public JwtObject login(LoginCredential loginCredential) {
        String code = loginCredential.getClientCode();
        verifyClientCode(code);

        Claims claims = jwtService.decodeJWT(code);
        final AppUserDto user = userService.getUserByUsername(claims.getIssuer());

        long tokenExpirationTime = getTokenExpirationTime();
        long refreshTokenExpirationTime = getRefreshTokenExpirationTime();
        final String accessToken = jwtService.createAccessToken(claims.getIssuer(), tokenExpirationTime,
                new ArrayList<>(), SecurityConstants.TOKEN_SECRET);
        final String refreshToken = jwtService.createRefreshToken(refreshTokenExpirationTime,
                SecurityConstants.TOKEN_SECRET);
        final JwtObject jwtObject = new JwtObject(user.getUsername(), accessToken, refreshToken, tokenExpirationTime,
                refreshTokenExpirationTime);

        setJwtToUserAndSave(user, accessToken, refreshToken);

        return jwtObject;
    }

    private void setJwtToUserAndSave(AppUserDto userDto, String token, String refreshToken) {
        userDto.setToken(token);
        userDto.setRefreshToken(refreshToken);
        userService.update(userDto);
    }

    private void verifyClientCode(String clientCode) {
        userService.verifyUserCode(clientCode);
    }

    public void logout(JwtObject jwtObject) {

        // if log out has been called, token need to be updated even on error occurs
        final AppUserDto appUserDto = userService.getUserByUsername(jwtObject.getUsername());
        updateNewTokensToUser(appUserDto, null, null);

        if (verifyIfUserSessionExpired(jwtObject.getToken_expire_time(), jwtObject.getRefresh_token_expire_time())) {
            throw new CustomException("Your session has been expired, please log in again.", HttpStatus.UNAUTHORIZED);
        }

    }

    private boolean verifyIfUserSessionExpired(long accessTokenExpirationTime, long refreshTokenExpirationTime) {

        final long currentTime = System.currentTimeMillis();
        return (currentTime > accessTokenExpirationTime && currentTime > refreshTokenExpirationTime);
    }

    @Transactional
    public JwtObject generateNewAccessToken(JwtObject refreshToken) {
        final AppUserDto appUserDto = userService.findUserByRefreshToken(refreshToken.getRefresh_token());

        JwtObject jwtObject = createNewJWtObject(appUserDto);
        updateNewTokensToUser(appUserDto, jwtObject.getAccess_token(), jwtObject.getRefresh_token());

        return jwtObject;

    }

    private void updateNewTokensToUser(AppUserDto appUserDto, String accessToken, String refreshToken) {

        appUserDto.setToken(accessToken);
        appUserDto.setRefreshToken(refreshToken);
        userService.update(appUserDto);
    }

    private JwtObject createNewJWtObject(AppUserDto appUserDto) {

        long tokenExpirationTime = getTokenExpirationTime();
        long refreshTokenExpirationTime = getRefreshTokenExpirationTime();
        final String accessToken = generateAccessToken(appUserDto);
        final String refreshToken = generateRefreshToken();

        JwtObject jwtObject = new JwtObject(appUserDto.getUsername(), accessToken, refreshToken, tokenExpirationTime,
                refreshTokenExpirationTime);

        return jwtObject;
    }

    private String generateRefreshToken() {
        long expirationTime = System.currentTimeMillis() + SecurityConstants.TOKEN_EXPIRATION_TIME;

        final String accessToken = jwtService.createRefreshToken(expirationTime, SecurityConstants.TOKEN_SECRET);

        return accessToken;
    }

    private String generateAccessToken(AppUserDto appUserDto) {
        final String issuer = appUserDto.getUsername();
        long expirationTime = getTokenExpirationTime();

        String accessToken = jwtService.createAccessToken(issuer, expirationTime, new ArrayList<>(),
                SecurityConstants.TOKEN_SECRET);

        return accessToken;
    }

    private Long getTokenExpirationTime() {
        return System.currentTimeMillis() + SecurityConstants.TOKEN_EXPIRATION_TIME;
    }

    private Long getRefreshTokenExpirationTime() {
        return System.currentTimeMillis() + SecurityConstants.REFRESH_TOKEN_EXPIRATION_TIME;
    }

    public void sendForgotPasswordEmail(String email) {
        Optional<AppUser> user = appUserRepository.findByEmail(email);

        if (user != null) {
            String forgotPasswordCode = generateRandomString();
            user.get().setForgotPasswordCode(forgotPasswordCode);
            appUserRepository.save(user.get());

            Map<String, Object> model = new HashMap<>();
            model.put("email", user.get().getEmail());
            model.put("forgotPasswordCode", user.get().getForgotPasswordCode());
            model.put("clientFrontedURL", env.getProperty("clientFrontedURL"));

            emailService.sendEmail("forgot_password.ftl", model, user.get().getEmail(), "Forgot password",
                    this.env.getProperty("mail.from"));
        }
    }

    private String generateRandomString() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

        return generatedString;
    }

    public void changePassword(String password, String confirmPassword, String email, String forgotPasswordCode) {
        if (!password.equals(confirmPassword)) {
            throw new CustomException("Passwords do not match", HttpStatus.BAD_REQUEST);
        }
        Optional<AppUser> user = appUserRepository.findByEmailAndForgotPasswordCode(email, forgotPasswordCode);

        if (!user.isPresent()) {
            throw new CustomException("Code invalid or wrong code for user", HttpStatus.BAD_REQUEST);
        }

        user.get().setPassword(passwordEncoder.encode(password));
        appUserRepository.save(user.get());
    }

}
