package com.antonio.authserver.controller;

import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.dto.RoleDto;
import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.mapper.RoleMapper;
import com.antonio.authserver.model.ResponseMessage;
import com.antonio.authserver.repository.RoleRepository;
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
    private RoleRepository roleRepository;


    @Autowired
    public UserController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public ResponseEntity<List<AppUserDto>> getUsers() {

        List<AppUserDto> users = userService.getAllUsers();
        return ResponseEntity.ok().body(users);
    }

    @GetMapping("/{username}")
    public ResponseEntity<AppUserDto> getUserByUsername(@PathVariable String username) {

        return ResponseEntity.ok().body(userService.getUserByUsername(username));
    }

    @PostMapping
    public ResponseEntity<?> saveUser(@RequestBody final AppUserDto user) {

        userService.save(user);
        final ResponseMessage responseMessage = new ResponseMessage("User successfully saved");

        return ResponseEntity.ok().body(responseMessage);
    }

    @PostMapping("/{id}/addRole")
    public void addRoleToUser(@PathVariable Long id, @RequestBody RoleDto role){
            Role newRole = roleRepository.findByName(role.getName());
            userService.addRole(id,newRole);

    }

    @DeleteMapping("/{id}/removeRole")
    public void removeRoleFromUser(@PathVariable Long id, @RequestBody RoleDto role){
        Role newRole = roleRepository.findByName(role.getName());
        userService.removeRole(id,newRole);

    }




}
