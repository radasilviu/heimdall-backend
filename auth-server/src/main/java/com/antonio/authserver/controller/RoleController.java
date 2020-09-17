package com.antonio.authserver.controller;

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
    public ResponseEntity<List<Role>> getRoles() {

        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok().body(roles);
    }


    @DeleteMapping
    public ResponseEntity<ResponseMessage> deleteRole(Long id) {

        roleService.deleteRoleById(id);
        final ResponseMessage responseMessage = new ResponseMessage("Role successfully deleted");
        return ResponseEntity.ok().body(responseMessage);

    }


    @PostMapping
    public ResponseEntity<ResponseMessage> createRole(@RequestBody Role role) {

        roleService.saveRole(role);
        final ResponseMessage responseMessage = new ResponseMessage("Role successfully saved");
        return ResponseEntity.ok().body(responseMessage);

    }


}
