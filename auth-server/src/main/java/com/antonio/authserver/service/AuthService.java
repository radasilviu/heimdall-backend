package com.antonio.authserver.service;

import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.model.Code;
import com.antonio.authserver.model.CustomException;
import com.antonio.authserver.model.JwtObject;
import com.antonio.authserver.model.LoginCredential;
import com.antonio.authserver.repository.AppUserRepository;
import com.antonio.authserver.request.ClientLoginRequest;
import com.antonio.authserver.utils.SecurityConstants;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class AuthService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final ClientService clientService;
    private final UserService userService;
    private final JwtService jwtService;
    private final Environment env;
    private final AppUserRepository appUserRepository;
    private final EmailService emailService;

    @Autowired
    public AuthService(BCryptPasswordEncoder passwordEncoder, ClientService clientService, UserService userService,
                       JwtService jwtService, Environment env,
                       AppUserRepository appUserRepository, EmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.clientService = clientService;
        this.userService = userService;
        this.jwtService = jwtService;
        this.env = env;
        this.appUserRepository = appUserRepository;
        this.emailService = emailService;
    }


    public Code getCode(ClientLoginRequest clientLoginRequest) {
        clientService.getClientBySecretAndNameWithRealm(clientLoginRequest.getRealm(),clientLoginRequest.getClientId(), clientLoginRequest.getClientSecret());
        final AppUserDto user = userService.findByUsernameAndPasswordAndRealm(clientLoginRequest.getUsername(),
                clientLoginRequest.getPassword(), clientLoginRequest.getRealm());


        Code code = clientService.generateCode(user);
        saveUserWithNewCodeValue(user, code);

        return code;
    }
    public void checkIfAccountIsActivated(ClientLoginRequest clientLoginRequest){
        AppUser appUser = appUserRepository.findByUsernameAndRealmName(clientLoginRequest.getUsername(), clientLoginRequest.getRealm()).orElseThrow(() -> new CustomException("The username could not be found!", HttpStatus.NOT_FOUND));
        if(!appUser.getIsActivated())
            throw new CustomException("The account is not activated!",HttpStatus.BAD_REQUEST);
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

        long tokenExpirationTime = jwtService.getTokenExpirationTime();
        long refreshTokenExpirationTime = jwtService.getRefreshTokenExpirationTime();
        final String accessToken = jwtService.createAccessToken(user, tokenExpirationTime,
                new ArrayList<>(), SecurityConstants.TOKEN_SECRET);
        final String refreshToken = jwtService.createRefreshToken(refreshTokenExpirationTime,
                SecurityConstants.TOKEN_SECRET);
        final JwtObject jwtObject = new JwtObject(user.getUsername(), accessToken, refreshToken, tokenExpirationTime,
                refreshTokenExpirationTime, user.getIdentityProvider().getProvider());

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

        long tokenExpirationTime = jwtService.getTokenExpirationTime();
        long refreshTokenExpirationTime = jwtService.getRefreshTokenExpirationTime();
        final String accessToken = generateAccessToken(appUserDto);
        final String refreshToken = generateRefreshToken();

        JwtObject jwtObject = new JwtObject(appUserDto.getUsername(), accessToken, refreshToken, tokenExpirationTime,
                refreshTokenExpirationTime, appUserDto.getIdentityProvider().getProvider());

        return jwtObject;
    }

    private String generateRefreshToken() {
        long expirationTime = System.currentTimeMillis() + SecurityConstants.TOKEN_EXPIRATION_TIME;

        final String accessToken = jwtService.createRefreshToken(expirationTime, SecurityConstants.TOKEN_SECRET);

        return accessToken;
    }

    private String generateAccessToken(AppUserDto appUserDto) {
        final String issuer = appUserDto.getUsername();
        long expirationTime = jwtService.getTokenExpirationTime();

        String accessToken = jwtService.createAccessToken(appUserDto, expirationTime, new ArrayList<>(),
                SecurityConstants.TOKEN_SECRET);

        return accessToken;
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

	public JwtObject profileLogin(String username, String password, String realm) {
		AppUserDto user = userService.findByUsernameAndRealmName(username, realm);
        JwtObject jwtObject = createNewJWtObject(user);

		return jwtObject;
	}
}
