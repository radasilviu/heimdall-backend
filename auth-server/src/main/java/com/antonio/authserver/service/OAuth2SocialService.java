package com.antonio.authserver.service;

import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.entity.IdentityProvider;
import com.antonio.authserver.model.Code;
import com.antonio.authserver.model.oauth.OAuthSocialUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class OAuth2SocialService {


    private UserService userService;
    private IdentityProviderService identityProviderService;
    private ClientService clientService;

    @Autowired
    public OAuth2SocialService(UserService userService, IdentityProviderService identityProviderService, ClientService clientService) {
        this.userService = userService;
        this.identityProviderService = identityProviderService;
        this.clientService = clientService;
    }

    public boolean verifyIfUserExist(String email) {
        return userService.verifyIfUserExist(email);
    }


    public Code getCode(OAuthSocialUser oAuthSocialUser) {

        verifyClientCredentials(oAuthSocialUser);

        AppUserDto appUserDto;
        if (verifyIfUserExist(oAuthSocialUser.getEmail())) {
            appUserDto = updateSocialUser(oAuthSocialUser);
        } else {
            appUserDto = registerSocialUser(oAuthSocialUser);
        }

        final Code code = clientService.generateCode(appUserDto);
        setGeneratedCodeToUser(appUserDto, code.getCode());

        return code;

    }

    private void setGeneratedCodeToUser(AppUserDto appUserDto, String code) {
        appUserDto.setCode(code);
        userService.update(appUserDto);
    }

    private void verifyClientCredentials(OAuthSocialUser oAuthSocialUser) {
        clientService.getClientBySecretAndNameWithRealm(oAuthSocialUser.getRealm(), oAuthSocialUser.getClientId(), oAuthSocialUser.getClientSecret());
    }


    private AppUserDto updateSocialUser(OAuthSocialUser oAuthSocialUser) {
        final AppUserDto appUserDto = userService.findByEmail(oAuthSocialUser.getEmail());
        final IdentityProvider identityProvider = identityProviderService.findByProvider(oAuthSocialUser.getProvider());
        appUserDto.setIdentityProvider(identityProvider);
        return userService.update(appUserDto);
    }

    private AppUserDto registerSocialUser(OAuthSocialUser oAuthSocialUser) {

        final AppUserDto appUserDto = createNewAppUser(oAuthSocialUser);
        AppUserDto savedAppUser = userService.createSocialUser(appUserDto);


        return savedAppUser;
    }

    private AppUserDto createNewAppUser(OAuthSocialUser oAuthSocialUser) {

        AppUserDto appUserDto = new AppUserDto();
        appUserDto.setUsername(oAuthSocialUser.getName());
        appUserDto.setEmail(oAuthSocialUser.getEmail());

        final IdentityProvider identityProvider = identityProviderService.findByProvider(oAuthSocialUser.getProvider());
        appUserDto.setIdentityProvider(identityProvider);

        return appUserDto;
    }
}
