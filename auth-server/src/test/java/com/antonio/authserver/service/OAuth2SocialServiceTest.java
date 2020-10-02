package com.antonio.authserver.service;

import com.antonio.authserver.configuration.constants.ErrorMessage;
import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.model.Code;
import com.antonio.authserver.model.CustomException;
import com.antonio.authserver.model.oauth.OAuthSocialUser;
import com.antonio.authserver.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class OAuth2SocialServiceTest {

    @Autowired
    private OAuth2SocialService oAuth2SocialService;

    @Autowired
    private AppUserRepository appUserRepository;


    private OAuthSocialUser socialUser_One;

    @BeforeEach
    public void setup() {
        socialUser_One = new OAuthSocialUser();
        socialUser_One.setName("Client1");
        socialUser_One.setClientId("myClient");
        socialUser_One.setClientSecret("clientPass");
        socialUser_One.setRealm("master0");
        socialUser_One.setProvider("USERNAME_AND_PASSWORD");
    }


    @Test
    void userWithGivenClientIdShouldNotBeAuthorized() {

        socialUser_One.setClientId("badClientId");
        CustomException exception = assertThrows(
                CustomException.class,
                () -> oAuth2SocialService.getCode(socialUser_One),
                "Expected getCode() to throw, but it didn't"
        );

        assertEquals(exception.getMessage(), ErrorMessage.INVALID_CLIENT.getMessage());
    }

    @Test
    void userWithGivenClientSecretShouldNotBeAuthorized() {

        socialUser_One.setClientSecret("badClientSecret");
        CustomException exception = assertThrows(
                CustomException.class,
                () -> oAuth2SocialService.getCode(socialUser_One),
                "Expected getCode() to throw, but it didn't"
        );

        assertEquals(exception.getMessage(), ErrorMessage.INVALID_CLIENT.getMessage());
    }

    @Test
    void userWithGivenRealmShouldNotBeAuthorized() {

        socialUser_One.setClientSecret("badRealm");
        CustomException exception = assertThrows(
                CustomException.class,
                () -> oAuth2SocialService.getCode(socialUser_One),
                "Expected getCode() to throw, but it didn't"
        );

        assertEquals(exception.getMessage(), ErrorMessage.INVALID_CLIENT.getMessage());
    }


    @Test
    void userWithValidClientCredentialShouldBeAuthoried() {

        Code code = oAuth2SocialService.getCode(socialUser_One);

        assertTrue(code != null);
    }


    @Test
    void assertThatSocialUserWillBeRegisterToSystem() {

        Optional<AppUser> appUserOptional = appUserRepository.findByEmail(socialUser_One.getEmail());
        assertThatUserDoNotExistInSystem(appUserOptional);

        oAuth2SocialService.getCode(socialUser_One);

        appUserOptional = appUserRepository.findByEmail(socialUser_One.getEmail());

        assertTrue(appUserOptional.isPresent());
        assertEquals(socialUser_One.getEmail(), appUserOptional.get().getEmail());

    }

    @Test
    void assertThatSocialUserWillBeUpdatedToSystem() {

        final String UPDATED_PROVIDER = "GOOGLE";

        Optional<AppUser> appUserOptional = appUserRepository.findByEmail(socialUser_One.getEmail());
        assertThatUserExistToSystem(appUserOptional);

        socialUser_One.setProvider(UPDATED_PROVIDER);
        oAuth2SocialService.getCode(socialUser_One);

        appUserOptional = appUserRepository.findByEmail(socialUser_One.getEmail());

        assertTrue(appUserOptional.isPresent());
        assertEquals(socialUser_One.getProvider(), UPDATED_PROVIDER);

    }

    @Test
    void assertThatSocialUserWithGivenProviderWouldNotBeUpdated() {

        final String UPDATED_PROVIDER = "BAD_PROVIDER";
        socialUser_One.setProvider(UPDATED_PROVIDER);

        CustomException exception = assertThrows(
                CustomException.class,
                () -> oAuth2SocialService.getCode(socialUser_One),
                "Expected getCode() to throw, but it didn't"
        );

        assertEquals(exception.getMessage(), ErrorMessage.IDENTITY_PROVIDER_NOT_FOUND.getMessage());
    }

    private void assertThatUserExistToSystem(Optional<AppUser> appUserOptional) {
        assertFalse(appUserOptional.isPresent());
    }

    private void assertThatUserDoNotExistInSystem(Optional<AppUser> appUserOptional) {
        assertTrue(appUserOptional.isPresent());
    }


}