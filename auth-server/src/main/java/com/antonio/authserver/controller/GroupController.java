package com.antonio.authserver.controller;

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

    @PostMapping("/group")
    public void createGroup(@RequestBody UserGroup userGroup) {
        groupService.createGroup(userGroup);
    }

    @GetMapping("/group")
    public List<GroupDto> getAllGroups() {
        return groupService.findAllGroups();
    }

    @GetMapping("/group/{name}")
    public GroupDto getGroupByName(@PathVariable String name) {
        return groupService.findGroupByName(name);
    }

    @DeleteMapping("/group/{name}")
    public void deleteGroupByName(@PathVariable String name) {
        groupService.deleteGroupByName(name);
    }

    @PutMapping("/group/{name}")
    public void updateByName(@PathVariable String name, @RequestBody GroupDto group) {
        groupService.updateByName(name, group);
    }
    @PostMapping("/group/{name}/addRole")
    public void addRoleToGroup(@PathVariable String name, @RequestBody Role role){
        groupService.addRoleForGroup(name,role);
    }
    @PostMapping("/group/{name}/addUser")
    public void addUserToGroup(@PathVariable String name, @RequestBody AppUser user){
        groupService.addUserToGroup(name,user);
    }
    @GetMapping("/group/{name}/users")
    public List<AppUser> getUsersFromGroup(@PathVariable String name){
        return groupService.getUsersFromGroup(name);
    }

}
