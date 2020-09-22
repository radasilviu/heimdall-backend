package com.antonio.authserver.controller;
import java.util.List;

import com.antonio.authserver.service.RoleService;
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
    private RoleService roleService;

    @Autowired
    public UserController(UserService userService,RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
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

	@PutMapping("/{username}")
	public ResponseEntity<ResponseMessage> updateUserByUsername(@PathVariable String username,
			@RequestBody AppUserDto appUserDto) {
		userService.updateUserByUsername(username, appUserDto);
		final ResponseMessage responseMessage = new ResponseMessage("User successfully updated");
		return ResponseEntity.ok().body(responseMessage);
	}

    @PostMapping("/{username}/addRole")
    public void addRoleToUser(@PathVariable String username, @RequestBody RoleDto role) {
        Role newRole = roleService.findRoleByNameDAO(role.getName());
        userService.addRole(username, newRole);
    }

	@DeleteMapping("/{username}")
	public void deleteUser(@PathVariable String username) {
		userService.deleteUser(username);
	}

    @DeleteMapping("/{username}/removeRole")
    public void removeRoleFromUser(@PathVariable String username, @RequestBody RoleDto role) {
        Role newRole = roleService.findRoleByNameDAO(role.getName());
        userService.removeRole(username, newRole);

	}

}
