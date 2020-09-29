package com.antonio.authserver.service;

import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.model.JwtObject;
import com.antonio.authserver.model.oauth.AuthenticationProvider;
import com.antonio.authserver.model.oauth.OAuth2CustomUser;
import com.antonio.authserver.utils.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static com.antonio.authserver.utils.JwtUtility.getRefreshTokenExpirationTime;
import static com.antonio.authserver.utils.JwtUtility.getTokenExpirationTime;

@Service
public class OAuth2SocialService {


    private UserService userService;
    private JwtService jwtService;


    @Autowired
    public OAuth2SocialService(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public void registerSocialUser(OAuth2CustomUser oAuth2CustomUser) {

        final AppUserDto appUserDto = createNewAppUser(oAuth2CustomUser);
        AppUserDto savedAppUser = userService.createSocialUser(appUserDto);

        final JwtObject jwtObject = generateNewToken(appUserDto.getUsername());
        savedAppUser.setToken(jwtObject.getAccess_token());
        savedAppUser.setRefreshToken(jwtObject.getRefresh_token());

        userService.update(savedAppUser);
    }

    private AppUserDto createNewAppUser(OAuth2CustomUser oAuth2CustomUser) {

        AppUserDto appUserDto = new AppUserDto();
        appUserDto.setUsername(oAuth2CustomUser.getName());
        appUserDto.setEmail(oAuth2CustomUser.getEmail());

        appUserDto.setAuthProvider(AuthenticationProvider.GOOGLE);

        return appUserDto;
    }


    public void updateSocialUser(String email) {
        final AppUserDto appUserDto = userService.findByEmail(email);

        final JwtObject jwtObject = generateNewToken(appUserDto.getUsername());

        updateNewTokensToUser(appUserDto, jwtObject.getAccess_token(), jwtObject.getRefresh_token());
    }

    private void updateNewTokensToUser(AppUserDto appUserDto, String accessToken, String refreshToken) {

        appUserDto.setToken(accessToken);
        appUserDto.setRefreshToken(refreshToken);
        appUserDto.setAuthProvider(AuthenticationProvider.GOOGLE);
        userService.update(appUserDto);
    }


    private JwtObject generateNewToken(String username) {
        long tokenExpirationTime = getTokenExpirationTime();
        long refreshTokenExpirationTime = getRefreshTokenExpirationTime();
        final String accessToken = jwtService.createAccessToken(username, tokenExpirationTime, new ArrayList<>(), SecurityConstants.TOKEN_SECRET);
        final String refreshToken = jwtService.createRefreshToken(refreshTokenExpirationTime, SecurityConstants.TOKEN_SECRET);
        final JwtObject jwtObject = new JwtObject(username, accessToken, refreshToken, tokenExpirationTime, refreshTokenExpirationTime);

        return jwtObject;
    }


    public boolean verifyIfUserExist(String email) {
        return userService.verifyIfUserExist(email);
    }


}
