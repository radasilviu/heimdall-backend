package com.antonio.authserver.controller;

import com.antonio.authserver.dto.GroupDto;
import com.antonio.authserver.entity.UserGroup;
import com.antonio.authserver.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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


}
