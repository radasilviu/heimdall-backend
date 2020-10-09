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
	private EmailService emailService;

	@Autowired
	public UserController(UserService userService, RoleService roleService,
			EmailService emailService) {
		this.userService = userService;
		this.roleService = roleService;
		this.emailService = emailService;
	}

	@GetMapping("/{realmName}")
	public ResponseEntity<List<AppUserDto>> getUsers(@PathVariable String realmName) {
		return ResponseEntity.ok().body(userService.getAllUsers(realmName));
	}

	@GetMapping("/{realmName}/{username}")
	public ResponseEntity<AppUserDto> getUserByUsername(@PathVariable String realmName,@PathVariable String username) {
		return ResponseEntity.ok().body(userService.getUserByUsernameAndRealmName(realmName,username));
	}

	@PostMapping("/{realmName}")
	public ResponseEntity<?> saveUser(@PathVariable String realmName,@RequestBody final AppUserDto user, HttpServletRequest request)
			throws IOException, MessagingException, TemplateException {
		String siteUrl = EmailUtility.getSiteUrl(request);
		userService.create(realmName,user);
		emailService.sendActivationEmail(user, siteUrl);
		final ResponseMessage responseMessage = new ResponseMessage("User successfully saved");
		return ResponseEntity.ok().body(responseMessage);
	}

	@PutMapping("/{realmName}/{username}")
	public ResponseEntity<ResponseMessage> updateUserByUsername(@PathVariable String realmName,@PathVariable String username,
			@RequestBody AppUserDto appUserDto) {
		userService.updateUserByUsername(realmName,username, appUserDto);
		final ResponseMessage responseMessage = new ResponseMessage("User successfully updated");
		return ResponseEntity.ok().body(responseMessage);
	}

	@PostMapping("/{realmName}/{username}/addRole")
	public void addRoleToUser(@PathVariable String realmName,@PathVariable String username, @RequestBody String roleName) {
		Role newRole = roleService.findRoleByNameDAO(roleName);
		userService.addRole(realmName,username, newRole);
	}

	@DeleteMapping("/{realmName}/{username}")
	public void deleteUser(@PathVariable String realmName,@PathVariable String username) {
		userService.deleteUser(realmName,username);
	}

	@DeleteMapping("/{realmName}/{username}/removeRole")
	public void removeRoleFromUser(@PathVariable String realmName,@PathVariable String username, @RequestBody String roleName) {
		Role newRole = roleService.findRoleByNameDAO(roleName);
		userService.removeRole(realmName,username, newRole);

	}

}
