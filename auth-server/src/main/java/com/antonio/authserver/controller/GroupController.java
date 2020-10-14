package com.antonio.authserver.controller;

import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.dto.GroupDto;
import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.entity.UserGroup;
import com.antonio.authserver.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.acl.Group;
import java.util.List;

@RestController
@RequestMapping("api/client")
public class GroupController {
    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/{realmName}/group")
    public List<GroupDto> getAllGroups(@PathVariable String realmName) {
        return groupService.findAllGroups(realmName);
    }

    @GetMapping("/{realmName}/group/{name}")
    public GroupDto getGroupByName(@PathVariable String realmName,@PathVariable String name) {
        return groupService.findGroupByName(name,realmName);
    }

    @PostMapping("/{realmName}/group")
    public void createGroup(@PathVariable String realmName,@RequestBody UserGroup userGroup) {
        groupService.createGroup(realmName,userGroup);
    }

    @PutMapping("/{realName}/group/{name}")
    public void updateByName(@PathVariable String realmName,@PathVariable String name, @RequestBody GroupDto group) {
        groupService.updateByName(realmName,name, group);
    }

    @DeleteMapping("/{realmName}/group/{name}")
    public void deleteGroupByName(@PathVariable String realmName,@PathVariable String name) {
        groupService.deleteGroupByName(realmName,name);
    }

    @PostMapping("/{realmName}/group/{name}/addRole/{roleName}")
    public void addRoleToGroup(@PathVariable String realmName,@PathVariable String name, @PathVariable String roleName){
        groupService.addRoleForGroup(realmName,name,roleName);
    }
    @PutMapping("/{realmName}/group/{name}/addUser")
    public void addUserToGroup(@PathVariable String realmName,@PathVariable String name, @RequestBody AppUser user){
        groupService.addUserToGroup(realmName,name,user);
    }
    @GetMapping("/{realmName}/group/{name}/users")
    public List<AppUserDto> getUsersFromGroup(@PathVariable String realmName,@PathVariable String name){
        return groupService.getUsersFromGroup(realmName,name);
    }
    @PutMapping("/{realmName}/group/{name}/deleteUser/{username}")
    public void deleteUserFromGroup(@PathVariable String realmName,@PathVariable String name, @PathVariable String username){
        groupService.deleteUserFromGroupByName(realmName,name, username);
    }

}
