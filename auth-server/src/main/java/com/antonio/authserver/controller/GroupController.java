package com.antonio.authserver.controller;

import com.antonio.authserver.dto.GroupDto;
import com.antonio.authserver.entity.Group;
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
    public void createGroup(@RequestBody Group group) {
        groupService.createGroup(group);
    }

    @GetMapping("/group")
    public List<GroupDto> getAllGroups() {
        return groupService.findAllGroups();
    }

    @GetMapping("/group/{name}")
    public GroupDto getGroupByName(@PathVariable String name) {
        return groupService.findGroupByName(name);
    }

    @GetMapping("/group/{name}")
    public void deleteGroupByName(@PathVariable String name) {
        groupService.deleteGroupByName(name);
    }


}
