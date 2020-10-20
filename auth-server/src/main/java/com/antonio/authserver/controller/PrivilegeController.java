package com.antonio.authserver.controller;
import com.antonio.authserver.dto.PrivilegeDto;
import com.antonio.authserver.dto.RoleDto;
import com.antonio.authserver.model.ResponseMessage;
import com.antonio.authserver.service.PrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
@RestController
@RequestMapping("api/privilege")
public class PrivilegeController {

    private final PrivilegeService privilegeService;

    @Autowired
    public PrivilegeController(PrivilegeService privilegeService) {
        this.privilegeService = privilegeService;
    }
    @GetMapping
    public ResponseEntity<List<PrivilegeDto>> getAllPrivileges(){
        return ResponseEntity.ok().body(privilegeService.getAllPrivileges());
    }
    @GetMapping("/{name}")
    public ResponseEntity<PrivilegeDto> getPrivilegeByName(@PathVariable String name){
        return ResponseEntity.ok().body(privilegeService.getPrivilegeByName(name));
    }
    @GetMapping("/{realmName}/{roleName}")
    public ResponseEntity<Set<PrivilegeDto>> getPrivilegesForRole(@PathVariable String realmName,@PathVariable String roleName){
        return ResponseEntity.ok().body(privilegeService.getPrivilegesForRole(realmName,roleName));
    }
    @PostMapping
    public ResponseEntity<ResponseMessage> createPrivilegesForNewResource(@RequestBody String resourceName){
        privilegeService.createPrivileges(resourceName);
        ResponseMessage responseMessage = new ResponseMessage("Privileges created successfully!");
        return ResponseEntity.ok().body(responseMessage);
    }
    @PutMapping("/{name}/{realmName}/{roleName}/add")
    public ResponseEntity<ResponseMessage> addPrivilegeToRole(@PathVariable String name, @PathVariable String realmName,@PathVariable String roleName){
        privilegeService.addPrivilegeToRole(name,realmName,roleName);
        ResponseMessage responseMessage = new ResponseMessage("Privilege added to role successfully!");
        return ResponseEntity.ok().body(responseMessage);
    }
    @PutMapping("/{name}/{realmName}/{roleName}/remove")
    public ResponseEntity<ResponseMessage> removePrivilegeFromRole(@PathVariable String name, @PathVariable String realmName,@PathVariable String roleName){
        privilegeService.removePrivilegeFromRole(name,realmName,roleName);
        ResponseMessage responseMessage = new ResponseMessage("Privilege removed from role successfully!");
        return ResponseEntity.ok().body(responseMessage);
    }

}
