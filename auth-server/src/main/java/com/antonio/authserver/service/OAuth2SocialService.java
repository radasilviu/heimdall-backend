package com.antonio.authserver.service;

import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.dto.IdentityProviderDto;
import com.antonio.authserver.model.Code;
import com.antonio.authserver.model.JwtObject;
import com.antonio.authserver.model.oauth.OAuthSocialUser;
import com.antonio.authserver.request.ClientLoginRequest;
import com.antonio.authserver.utils.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static com.antonio.authserver.utils.JwtUtility.getRefreshTokenExpirationTime;
import static com.antonio.authserver.utils.JwtUtility.getTokenExpirationTime;

@Service
public class OAuth2SocialService {


    private UserService userService;
    private IdentityProviderService identityProviderService;

    @Autowired
    public OAuth2SocialService(UserService userService, IdentityProviderService identityProviderService) {
        this.userService = userService;
        this.identityProviderService = identityProviderService;
    }


    private void updateNewTokensToUser(AppUserDto appUserDto, String accessToken, String refreshToken) {

        appUserDto.setToken(accessToken);
        appUserDto.setRefreshToken(refreshToken);

        final IdentityProviderDto identityProviderDto = identityProviderService.findByProvider("GOOGLE");
        appUserDto.setIdentityProvider(identityProviderDto);

        userService.update(appUserDto);
    }

    public boolean verifyIfUserExist(String email) {
        return userService.verifyIfUserExist(email);
    }


    public Code getCode(OAuthSocialUser oAuthSocialUser) {

        if (verifyIfUserExist(oAuthSocialUser.getEmail())) {
            updateSocialUser(oAuthSocialUser.getEmail());
        } else {
            registerSocialUser(oAuthSocialUser);
        }
        return null;
    }


    private void updateSocialUser(String email) {
        final AppUserDto appUserDto = userService.findByEmail(email);

    }

    private void registerSocialUser(OAuthSocialUser oAuthSocialUser) {

        final AppUserDto appUserDto = createNewAppUser(oAuthSocialUser);
        AppUserDto savedAppUser = userService.createSocialUser(appUserDto);


        userService.update(savedAppUser);
    }

    private AppUserDto createNewAppUser(OAuthSocialUser oAuthSocialUser) {

        AppUserDto appUserDto = new AppUserDto();
        appUserDto.setUsername(oAuthSocialUser.getName());
        appUserDto.setEmail(oAuthSocialUser.getEmail());

        final IdentityProviderDto identityProviderDto = identityProviderService.findByProvider("GOOGLE");
        appUserDto.setIdentityProvider(identityProviderDto);

        return appUserDto;
    }
}
