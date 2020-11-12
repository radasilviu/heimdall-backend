package com.antonio.authserver.controller;


import com.antonio.authserver.model.AdminCredential;
import com.antonio.authserver.model.JwtObject;
import com.antonio.authserver.model.ResponseMessage;
import com.antonio.authserver.service.AdminService;
import com.antonio.authserver.service.AuthService;
import lombok.var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.stream.Collectors;

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


    @GetMapping("/setCredentials/{username}")
    public void setCookie(HttpServletResponse response, @PathVariable String username) {
        adminService.setCookie(response, username);
    }

//    @GetMapping("/all-cookies")
//    public String readAllCookies(HttpServletRequest request) {
//
//        System.out.println(request.getHeader( "Cookie" ));
//        System.out.println(request.getCookies());
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            return Arrays.stream(cookies)
//                    .map(c -> c.getName() + "=" + c.getValue()).collect(Collectors.joining(", "));
//        }
//
//        return "No cookies";
//    }



}
