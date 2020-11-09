package com.antonio.authserver.controller;

import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.model.ResponseMessage;
import com.antonio.authserver.service.EmailService;
import com.antonio.authserver.service.RoleService;
import com.antonio.authserver.service.UserService;
import com.antonio.authserver.utils.EmailUtility;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/user")
public class UserController {

    private UserService userService;
    private RoleService roleService;
    private EmailService emailService;

    @Autowired
    public UserController(UserService userService, RoleService roleService,
                          EmailService emailService) {
        this.userService = userService;
        this.roleService = roleService;
        this.emailService = emailService;
    }

    @GetMapping("/{realmName}")
    public ResponseEntity<List<AppUserDto>> getUsers(@PathVariable String realmName, HttpServletResponse response) {
        return ResponseEntity.ok().body(userService.getAllUsers(realmName));
    }


    @GetMapping("/{realmName}/{username}")
    public ResponseEntity<AppUserDto> getUserByUsername(@PathVariable String realmName, @PathVariable String username) {
        return ResponseEntity.ok().body(userService.getUserByUsernameAndRealmName(realmName, username));
    }

    @PostMapping("/{realmName}")
    public ResponseEntity<?> saveUser(@PathVariable String realmName, @RequestBody final AppUserDto user, HttpServletRequest request)
            throws IOException, MessagingException, TemplateException {
        String siteUrl = EmailUtility.getSiteUrl(request);
        userService.create(realmName, user);
        emailService.sendActivationEmail(user, siteUrl);
        final ResponseMessage responseMessage = new ResponseMessage("User successfully saved");
        return ResponseEntity.ok().body(responseMessage);
    }

    @PutMapping("/{realmName}/{username}")
    public ResponseEntity<ResponseMessage> updateUserByUsername(@PathVariable String realmName, @PathVariable String username,
                                                                @RequestBody AppUserDto appUserDto) {
        userService.updateUserByUsername(realmName, username, appUserDto);
        final ResponseMessage responseMessage = new ResponseMessage("User successfully updated");
        return ResponseEntity.ok().body(responseMessage);
    }

    @PostMapping("/{realmName}/{username}/addRole")
    public void addRoleToUser(@PathVariable String realmName, @PathVariable String username, @RequestBody String roleName) {
        Role newRole = roleService.findRoleByNameDaoAndRealmName(roleName, realmName);
        userService.addRole(realmName, username, newRole);
    }

    @DeleteMapping("/{realmName}/{username}")
    public void deleteUser(@PathVariable String realmName, @PathVariable String username) {
        userService.deleteUser(realmName, username);
    }

    @DeleteMapping("/{realmName}/{username}/removeRole")
    public void removeRoleFromUser(@PathVariable String realmName, @PathVariable String username, @RequestBody String roleName) {
        Role newRole = roleService.findRoleByNameDaoAndRealmName(roleName, realmName);
        userService.removeRole(realmName, username, newRole);

    }

    @GetMapping("/{realmName}/getUsersWithoutAdmin")
    public List<AppUserDto> getUsersWithoutAdmin(@PathVariable String realmName) {
        return userService.getUsersWithoutAdmins(realmName);
    }

}
