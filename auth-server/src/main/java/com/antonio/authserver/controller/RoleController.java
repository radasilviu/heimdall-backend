package com.antonio.authserver.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.antonio.authserver.dto.RoleDto;
import com.antonio.authserver.model.ResponseMessage;
import com.antonio.authserver.service.RoleService;

@RestController
@RequestMapping("api/role")
@CrossOrigin
public class RoleController {

	private RoleService roleService;

	@Autowired
	public RoleController(RoleService roleService) {
		this.roleService = roleService;
	}

	@GetMapping
	public ResponseEntity<List<RoleDto>> getRoles() {
		List<RoleDto> roles = roleService.getAllRoles();
		return ResponseEntity.ok().body(roles);
	}
	@GetMapping("/{name}")
	public ResponseEntity<RoleDto> getRoleByName(@PathVariable String name) {
		return ResponseEntity.ok().body(roleService.getRoleByName(name));
	}
	@PutMapping("/{name}")
	public ResponseEntity<ResponseMessage> updateRoleByName(@PathVariable String name, @RequestBody RoleDto roleDto) {
		roleService.updateRoleByName(name, roleDto);
		final ResponseMessage responseMessage = new ResponseMessage("Role successfully updated");
		return ResponseEntity.ok().body(responseMessage);
	}
	@PostMapping
	public ResponseEntity<ResponseMessage> createRole(@RequestBody RoleDto role) {
		roleService.saveRole(role);
		final ResponseMessage responseMessage = new ResponseMessage("Role successfully saved");
		return ResponseEntity.ok().body(responseMessage);
	}

	@DeleteMapping("/{name}")
	public ResponseEntity<ResponseMessage> deleteRole(@PathVariable String name) {
		roleService.deleteRoleByName(name);
		final ResponseMessage responseMessage = new ResponseMessage("Role successfully deleted");
		return ResponseEntity.ok().body(responseMessage);

	}


}
