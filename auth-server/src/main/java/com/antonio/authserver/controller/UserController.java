package com.antonio.authserver.controller;

import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.model.ResponseMessage;
import com.antonio.authserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {


    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<AppUser>> getUsers() {

        List<AppUser> users = userService.getAllUsers();
        return ResponseEntity.ok().body(users);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {

        AppUser user = userService.getUserByUsername(username);

        return ResponseEntity.ok().body(user);
    }

    @PostMapping
    public ResponseEntity<?> saveUser(@RequestBody final AppUser user) {

        userService.save(user);
        final ResponseMessage responseMessage = new ResponseMessage("User successfully saved");

        return ResponseEntity.ok().body(responseMessage);
    }

    @PostMapping("/{id}/addRole")
    public void addRoleToUser(@PathVariable Long user,@RequestBody Role role){
        userService.addRole((long) 1,role);
    }


}
