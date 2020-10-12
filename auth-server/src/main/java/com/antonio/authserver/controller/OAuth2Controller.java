package com.antonio.authserver.controller;

import com.antonio.authserver.model.Code;
import com.antonio.authserver.model.ResponseMessage;
import com.antonio.authserver.model.oauth.OAuthSocialUser;
import com.antonio.authserver.service.OAuth2SocialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("oauth")
public class OAuth2Controller {

    @Autowired
    private OAuth2SocialService oAuth2SocialService;

    @PostMapping(path = "/social-login")
    public ResponseEntity<?> socialLogin(@RequestBody OAuthSocialUser oAuthSocialUser) {

        Code code = oAuth2SocialService.getCode(oAuthSocialUser);
        if (code != null) {
            return ResponseEntity.ok().body(code);
        }
        final ResponseMessage responseMessage = new ResponseMessage("Failed to generate JWT Code");
        return ResponseEntity.unprocessableEntity().body(responseMessage);
    }

}
