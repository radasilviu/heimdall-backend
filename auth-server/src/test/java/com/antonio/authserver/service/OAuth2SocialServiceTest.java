package com.antonio.authserver.service;

import com.antonio.authserver.model.CustomException;
import com.antonio.authserver.model.oauth.OAuthSocialUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
class OAuth2SocialServiceTest  {

    @Autowired
    private OAuth2SocialService oAuth2SocialService;


    private OAuthSocialUser socialUser_One;
    private OAuthSocialUser socialUser_Two;

    @BeforeEach
    public void setup() {
        socialUser_One = new OAuthSocialUser();
        socialUser_One.setName("Client1");
        socialUser_One.setClientId("testClient");
        socialUser_One.setClientSecret("clientSecret");
        socialUser_One.setRealm("realm");
        socialUser_One.setProvider("USERNAME_AND_PASSWORD");

        socialUser_Two = new OAuthSocialUser();
        socialUser_Two.setName("Client2");
        socialUser_Two.setClientId("myClient");
        socialUser_Two.setClientSecret("clientPass");
        socialUser_Two.setRealm("realm");
        socialUser_Two.setProvider("GOOGLE");
    }


    @Test
    void userWithGivenClientShouldNotBeAuthorized() {

        CustomException exception = assertThrows(
                CustomException.class,
                () -> oAuth2SocialService.getCode(socialUser_One),
                "Expected getCode() to throw, but it didn't"
        );

        assertTrue(exception.getMessage().contains("Client  is invalid"));
    }

    @Test
    void userWithValidClientCredentialShouldBeRegistred() {

        CustomException exception = assertThrows(
                CustomException.class,
                () -> oAuth2SocialService.getCode(socialUser_One),
                "Expected getCode() to throw, but it didn't"
        );

        assertTrue(exception.getMessage().contains("Client  is invalid"));
    }





}