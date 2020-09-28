package com.antonio.authserver.controller;


import com.antonio.authserver.model.AdminCredential;
import com.antonio.authserver.model.JwtObject;
import com.antonio.authserver.model.ResponseMessage;
import com.antonio.authserver.service.AdminService;
import com.antonio.authserver.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin")
public class AdminController {

    private AdminService adminService;
    private AuthService authService;

    @Autowired
    public AdminController(AdminService adminService, AuthService authService) {
        this.adminService = adminService;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AdminCredential adminCredential) {
        JwtObject jwtObject = adminService.adminLogin(adminCredential);

        return ResponseEntity.ok().body(jwtObject);
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<?> logout(@RequestBody JwtObject jwtObject) {
        authService.logout(jwtObject);
        final ResponseMessage responseMessage = new ResponseMessage("User logged out");
        return ResponseEntity.ok().body(responseMessage);
    }

    @PutMapping(path = "/refreshToken")
    public ResponseEntity<?> getNewTokenByRefreshToken(@RequestBody JwtObject refreshToken) {
        JwtObject jwtObject = authService.generateNewAccessToken(refreshToken);
        return ResponseEntity.ok().body(jwtObject);
    }


}
