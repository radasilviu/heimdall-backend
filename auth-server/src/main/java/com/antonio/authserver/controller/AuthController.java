package com.antonio.authserver.controller;

import com.antonio.authserver.model.Code;
import com.antonio.authserver.request.ClientLoginRequest;
import com.antonio.authserver.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @CrossOrigin("http://localhost:4201")
    @PostMapping(path = "/client-login")
    public Code generateCode(@RequestBody ClientLoginRequest loginRequest) {
        Code code = authService.getCode(loginRequest);
        return code;
    }
}
