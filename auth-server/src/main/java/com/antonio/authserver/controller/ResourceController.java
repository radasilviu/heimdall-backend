package com.antonio.authserver.controller;
import com.antonio.authserver.dto.PrivilegeDto;
import com.antonio.authserver.dto.ResourceDto;
import com.antonio.authserver.dto.RoleDto;
import com.antonio.authserver.model.ResponseMessage;
import com.antonio.authserver.service.PrivilegeService;
import com.antonio.authserver.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController("api/resources")
public class ResourceController {

    private final ResourceService resourceService;
    private final PrivilegeService privilegeService;

    @Autowired
    public ResourceController(ResourceService resourceService, PrivilegeService privilegeService) {
        this.resourceService = resourceService;
        this.privilegeService = privilegeService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<ResourceDto>> getAllResources(){
        return ResponseEntity.ok().body(resourceService.getAllResources());
    }
    @GetMapping
    public ResponseEntity<List<ResourceDto>> getAllResourcesForRoleDto(@RequestBody RoleDto roleDto){
        return ResponseEntity.ok().body(resourceService.getAllResourcesForRole(roleDto));
    }
    @GetMapping("/{resourceName}")
    public ResponseEntity<List<PrivilegeDto>> getPrivilegesForResource(@PathVariable String resourceName, @RequestBody RoleDto roleDto){
        return ResponseEntity.ok().body(privilegeService.getPrivilegesForResource(resourceName,roleDto));
    }
    @PostMapping
    public ResponseEntity<ResponseMessage> addResourceToDatabase(@RequestBody ResourceDto resourceDto,@RequestBody RoleDto roleDto){
        resourceService.createResourceForRole(resourceDto,roleDto);
        ResponseMessage responseMessage = new ResponseMessage("Resource created successfully!");
        return ResponseEntity.ok().body(responseMessage);
    }
    @PutMapping("/{realmName}/{roleName}/{resourceName}/add")
    public ResponseEntity<ResponseMessage> addResourceToRoleDto(@PathVariable String realmName,@PathVariable String roleName, @PathVariable String resourceName){
        resourceService.addResourceToRole(realmName,roleName,resourceName);
        ResponseMessage responseMessage = new ResponseMessage("Resource added successfully!");
        return ResponseEntity.ok().body(responseMessage);
    }
    @PutMapping("/{realmName}/{roleName}/{resourceName}/remove")
    public ResponseEntity<ResponseMessage> removeResourceFromRoleDto(@PathVariable String realmName,@PathVariable String roleName, @PathVariable String resourceName){
        resourceService.removeResourceFromRole(realmName,roleName,resourceName);
        ResponseMessage responseMessage = new ResponseMessage("Resource removed successfully!");
        return ResponseEntity.ok().body(responseMessage);
    }
    @DeleteMapping("/{resourceName}")
    public ResponseEntity<ResponseMessage> removeResourceFromDatabase(@PathVariable String resourceName,@RequestBody RoleDto roleDto){
        resourceService.deleteResourceForRole(resourceName,roleDto);
        ResponseMessage responseMessage = new ResponseMessage("Resource deleted successfully!");
        return ResponseEntity.ok().body(responseMessage);
    }
}
