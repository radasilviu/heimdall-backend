package com.antonio.authserver.mapper;

import com.antonio.authserver.dto.GroupDto;
import com.antonio.authserver.entity.UserGroup;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class GroupMapperClass {
    private UserGroup userGroup;
    private GroupDto groupDto;

    public GroupDto daoToDto(UserGroup userGroup){
        return new GroupDto(userGroup.getName(),userGroup.getAppUserGroup());
    }

    public List<GroupDto> daoListToDto(List<UserGroup> groups){
        List<GroupDto> groupDtos = new ArrayList<>();
        for (UserGroup group: groups){
            groupDtos.add(new GroupDto(group.getName(),group.getAppUserGroup()));
        }
        return groupDtos;
    }
}
