package com.antonio.authserver.controller;
import java.io.IOException;
import java.util.List;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.dto.RoleDto;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.model.ResponseMessage;
import com.antonio.authserver.service.RoleService;
import com.antonio.authserver.repository.RoleRepository;
import com.antonio.authserver.service.EmailService;
import com.antonio.authserver.service.RoleService;
import com.antonio.authserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.antonio.authserver.utils.EmailUtility;
import freemarker.template.TemplateException;
@RestController
@RequestMapping("api/user")
public class UserController {

	private UserService userService;
	private RoleService roleService;
	private PasswordEncoder passwordEncoder;
	private RoleRepository roleRepository;
	private EmailService emailService;

	@Autowired
	public UserController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder,
			EmailService emailService) {
		this.userService = userService;
		this.roleService = roleService;
		this.passwordEncoder = passwordEncoder;
		this.emailService = emailService;
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
	public ResponseEntity<?> saveUser(@RequestBody final AppUserDto user, HttpServletRequest request)
			throws IOException, MessagingException, TemplateException {
		String siteUrl = EmailUtility.getSiteUrl(request);
		userService.create(user);
		emailService.sendActivationEmail(user, siteUrl);
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
	public void addRoleToUser(@PathVariable String username, @RequestBody String roleName) {
		Role newRole = roleService.findRoleByNameDAO(roleName);
		userService.addRole(username, newRole);
	}

	@DeleteMapping("/{username}")
	public void deleteUser(@PathVariable String username) {
		userService.deleteUser(username);
	}

	@DeleteMapping("/{username}/removeRole")
	public void removeRoleFromUser(@PathVariable String username, @RequestBody String roleName) {
		Role newRole = roleService.findRoleByNameDAO(roleName);
		userService.removeRole(username, newRole);

	}

}
