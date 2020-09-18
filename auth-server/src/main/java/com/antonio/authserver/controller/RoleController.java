package com.antonio.authserver.controller;

import com.antonio.authserver.dto.RoleDto;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.model.ResponseMessage;
import com.antonio.authserver.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
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


    @DeleteMapping
    public ResponseEntity<ResponseMessage> deleteRole(String name) {

        roleService.deleteRoleByName(name);
        final ResponseMessage responseMessage = new ResponseMessage("Role successfully deleted");
        return ResponseEntity.ok().body(responseMessage);

    }


    @PostMapping
    public ResponseEntity<ResponseMessage> createRole(@RequestBody RoleDto role) {

        roleService.saveRole(role);
        final ResponseMessage responseMessage = new ResponseMessage("Role successfully saved");
        return ResponseEntity.ok().body(responseMessage);

    }


}
