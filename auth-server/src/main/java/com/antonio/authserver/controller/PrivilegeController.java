package com.antonio.authserver.controller;
import com.antonio.authserver.dto.PrivilegeDto;
import com.antonio.authserver.dto.RoleDto;
import com.antonio.authserver.model.ResponseMessage;
import com.antonio.authserver.service.PrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    @GetMapping("/{resourceName}")
    public ResponseEntity<List<PrivilegeDto>> getPrivilegesForRole(@PathVariable String resourceName,@RequestBody RoleDto roleDto){
        return ResponseEntity.ok().body(privilegeService.getPrivilegesForResource(resourceName,roleDto));
    }
    @PutMapping("/{privilegeName}/{resourceName}/add")
    public ResponseEntity<ResponseMessage> addPrivilegeToRole(@PathVariable String privilegeName, @PathVariable String resourceName,@RequestBody RoleDto role){
        privilegeService.addPrivilegeToResourceForRole(privilegeName,resourceName,role);
        ResponseMessage responseMessage = new ResponseMessage("Privilege added to role successfully!");
        return ResponseEntity.ok().body(responseMessage);
    }
    @PutMapping("/{privilegeName}/{resourceName}/remove")
    public ResponseEntity<ResponseMessage> removePrivilegeFromRole(@PathVariable String privilegeName, @PathVariable String resourceName,@RequestBody RoleDto role){
        privilegeService.removePrivilegeFromRole(privilegeName,resourceName,role);
        ResponseMessage responseMessage = new ResponseMessage("Privilege removed from role successfully!");
        return ResponseEntity.ok().body(responseMessage);
    }

}
