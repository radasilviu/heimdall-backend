package com.antonio.authserver.controller;

import com.antonio.authserver.dto.RoleDto;
import com.antonio.authserver.model.ResponseMessage;
import com.antonio.authserver.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//REALM
@RestController
@RequestMapping("api/role")
public class RoleController {

	private RoleService roleService;

	@Autowired
	public RoleController(RoleService roleService) {
		this.roleService = roleService;
	}

	@GetMapping("{realmName}")
	public ResponseEntity<List<RoleDto>> getRoles(@PathVariable String realmName) {
		List<RoleDto> roles = roleService.getAllRoles(realmName);
		return ResponseEntity.ok().body(roles);
	}
	@GetMapping("/{realmName}/{name}")
	public ResponseEntity<RoleDto> getRoleByName(@PathVariable String realmName,@PathVariable String name) {
		return ResponseEntity.ok().body(roleService.getRoleByName(realmName,name));
	}
	@PutMapping("/{realmName}/{name}")
	public ResponseEntity<ResponseMessage> updateRoleByName(@PathVariable String realmName,@PathVariable String name, @RequestBody RoleDto roleDto) {
		roleService.updateRoleByName(realmName,name, roleDto);
		final ResponseMessage responseMessage = new ResponseMessage("Role successfully updated");
		return ResponseEntity.ok().body(responseMessage);
	}
	@PostMapping("/{realmName}")
	public ResponseEntity<ResponseMessage> createRole(@PathVariable String realmName,@RequestBody RoleDto role) {
		roleService.saveRole(realmName,role);
		final ResponseMessage responseMessage = new ResponseMessage("Role successfully saved");
		return ResponseEntity.ok().body(responseMessage);
	}

	@DeleteMapping("/{realmName}/{name}")
	public ResponseEntity<ResponseMessage> deleteRole(@PathVariable String realmName,@PathVariable String name) {
		roleService.deleteRoleByName(realmName,name);
		final ResponseMessage responseMessage = new ResponseMessage("Role successfully deleted");
		return ResponseEntity.ok().body(responseMessage);

	}


}
