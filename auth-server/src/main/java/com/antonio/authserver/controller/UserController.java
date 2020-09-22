package com.antonio.authserver.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.dto.RoleDto;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.model.ResponseMessage;
import com.antonio.authserver.repository.RoleRepository;
import com.antonio.authserver.service.UserService;

@RestController
@RequestMapping("api/user")
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
        userService.create(user);
        final ResponseMessage responseMessage = new ResponseMessage("User successfully saved");
        return ResponseEntity.ok().body(responseMessage);
    }

    @PutMapping
    public ResponseEntity<ResponseMessage> updateUserByUsername(@RequestBody AppUserDto appUserDto) {
        userService.update(appUserDto);
        final ResponseMessage responseMessage = new ResponseMessage("User successfully updated");
        return ResponseEntity.ok().body(responseMessage);
    }

    @PostMapping("/{username}/addRole")
    public void addRoleToUser(@PathVariable String username, @RequestBody RoleDto role) {
        Role newRole = roleRepository.findByName(role.getName()).get();
        userService.addRole(username, newRole);
    }

    @DeleteMapping("/{username}")
    public void deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
    }

    @DeleteMapping("/{username}/removeRole")
    public void removeRoleFromUser(@PathVariable String username, @RequestBody RoleDto role) {
        Role newRole = roleRepository.findByName(role.getName()).get();
        userService.removeRole(username, newRole);

    }

}
