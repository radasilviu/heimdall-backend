package com.antonio.authserver.controller;

import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.model.Code;
import com.antonio.authserver.model.JwtObject;
import com.antonio.authserver.model.LoginCredential;
import com.antonio.authserver.model.ResponseMessage;
import com.antonio.authserver.request.ClientLoginRequest;
import com.antonio.authserver.service.AuthService;
import com.antonio.authserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    public UserService userService;

    @PostMapping(path = "/client-login")
    public ResponseEntity<?> clientLogin(@RequestBody ClientLoginRequest loginRequest) {
        Code code = authService.getCode(loginRequest);

        if (code != null) {
            return ResponseEntity.ok().body(code);
        }
        final ResponseMessage responseMessage = new ResponseMessage("Failed to generate JWT Code");
        return ResponseEntity.unprocessableEntity().body(responseMessage);
    }

    @PostMapping(path = "/token")
    public ResponseEntity<?> getToken(@RequestBody LoginCredential loginCredential) {
        JwtObject jwtObject = authService.login(loginCredential);
        return ResponseEntity.ok().body(jwtObject);
    }

    @PutMapping(path = "/refreshToken")
    public ResponseEntity<?> getNewTokenByRefreshToken(@RequestBody JwtObject refreshToken) {
        JwtObject jwtObject = authService.generateNewAccessToken(refreshToken);
        return ResponseEntity.ok().body(jwtObject);
    }

    @PostMapping(path = "/token/delete")
    public ResponseEntity<?> deleteToken(@RequestBody JwtObject jwtObject) {
        authService.logout(jwtObject);
        final ResponseMessage responseMessage = new ResponseMessage("User loogged out");
        return ResponseEntity.ok().body(responseMessage);
    }

    @GetMapping(path = "/access")
    public ResponseEntity<?> verifyToken() {
        final ResponseMessage responseMessage = new ResponseMessage("Valid Token");
        return ResponseEntity.ok().body(responseMessage);
    }

    @PostMapping("register")
    public ResponseEntity<?> saveUser(@RequestBody final AppUserDto user) {
        userService.create(user);
        final ResponseMessage responseMessage = new ResponseMessage("User successfully saved");
        return ResponseEntity.ok().body(responseMessage);
    }


}
