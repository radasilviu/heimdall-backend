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

import javax.transaction.Transactional;
import java.util.List;
@RestController
@RequestMapping("api/resources")
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

    @PostMapping("/getResources")
    public ResponseEntity<List<ResourceDto>> getAllResourcesForRoleDto(@RequestBody RoleDto roleDto){
        return ResponseEntity.ok().body(resourceService.getAllResourcesForRole(roleDto));
    }

    @PostMapping("/{resourceName}")
    public ResponseEntity<List<PrivilegeDto>> getPrivilegesForResource(@PathVariable String resourceName, @RequestBody RoleDto roleDto){
        return ResponseEntity.ok().body(privilegeService.getPrivilegesForResource(resourceName, roleDto));
    }

    @PostMapping
    public ResponseEntity<ResponseMessage> addResourceToDatabase(@RequestBody ResourceDto resourceDto){
        resourceService.createResourceForAllRoles(resourceDto);
        ResponseMessage responseMessage = new ResponseMessage("Resource created successfully!");
        return ResponseEntity.ok().body(responseMessage);
    }

//    // This could just as well be a GET, we don't use the body
//    @PutMapping("/{resourceName}/add")
//    public ResponseEntity<ResponseMessage> addResourceToRoleDto(@PathVariable String resourceName, @RequestBody Rol){
//        resourceService.addResourceToRole(realmName,roleName,resourceName);
//        ResponseMessage responseMessage = new ResponseMessage("Resource added successfully!");
//        return ResponseEntity.ok().body(responseMessage);
//    }
//
//    // This could just as well be a GET, we don't use the body; See removeResourceFromRole
//    @PutMapping("/{realmName}/{roleName}/{resourceName}/remove")
//    public ResponseEntity<ResponseMessage> removeResourceFromRoleDto(@PathVariable String realmName,@PathVariable String roleName, @PathVariable String resourceName){
//        resourceService.removeResourceFromRole(realmName,roleName,resourceName);
//        ResponseMessage responseMessage = new ResponseMessage("Resource removed successfully!");
//        return ResponseEntity.ok().body(responseMessage);
//    }

    @DeleteMapping("/{resourceName}/removeAll")
    public ResponseEntity<ResponseMessage> removeResourceFromDatabase(@PathVariable String resourceName){
        resourceService.deleteResourceForAllRoles(resourceName);
        ResponseMessage responseMessage = new ResponseMessage("Resource deleted successfully!");
        return ResponseEntity.ok().body(responseMessage);
    }

    @DeleteMapping("/{resourceName}")
    public ResponseEntity<ResponseMessage> removeResourceFromRole(@PathVariable String resourceName, @RequestBody RoleDto roleDto){
        resourceService.deleteResourceForRole(resourceName, roleDto);
        ResponseMessage responseMessage = new ResponseMessage("Resource deleted successfully for role!");
        return ResponseEntity.ok().body(responseMessage);
    }
}
