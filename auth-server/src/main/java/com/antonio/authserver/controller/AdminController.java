package com.antonio.authserver.controller;


import com.antonio.authserver.model.AdminCredential;
import com.antonio.authserver.model.JwtObject;
import com.antonio.authserver.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("admin")
public class AdminController {

    private AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(AdminCredential adminCredential) {
        JwtObject jwtObject = adminService.validateAdminService(adminCredential);


        return ResponseEntity.ok().body(jwtObject);
    }
}
