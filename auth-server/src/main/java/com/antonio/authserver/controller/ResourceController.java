package com.antonio.authserver.controller;
import com.antonio.authserver.dto.ResourceDto;
import com.antonio.authserver.dto.RoleDto;
import com.antonio.authserver.model.ResponseMessage;
import com.antonio.authserver.service.ResourceService;
import com.antonio.authserver.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
@RestController
@RequestMapping("api/resources")
public class ResourceController {

    private final ResourceService resourceService;
    @Autowired
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<ResourceDto>> getAllAvailableResources(){
        return ResponseEntity.ok().body(resourceService.getAllResourcesFromDb());
    }
    @GetMapping("/{realmName}/{roleName}")
    public ResponseEntity<Set<ResourceDto>> getAssignedResourcesForRole(@PathVariable String realmName, @PathVariable String roleName){
        return ResponseEntity.ok().body(resourceService.getAllResourcesForRole(realmName,roleName));
    }
    @PostMapping
    public ResponseEntity<ResponseMessage> createResource(@RequestBody ResourceDto resourceDto){
        resourceService.addResourceToDb(resourceDto);
        ResponseMessage responseMessage = new ResponseMessage("Resource created successfully!");
        return ResponseEntity.ok().body(responseMessage);
    }
    @DeleteMapping("/{resourceName}/removeAll")
    public ResponseEntity<ResponseMessage> removeResourceFromDatabase(@PathVariable String resourceName){
        resourceService.removeResourceFromDb(resourceName);
        ResponseMessage responseMessage = new ResponseMessage("Resource deleted successfully!");
        return ResponseEntity.ok().body(responseMessage);
    }

}
