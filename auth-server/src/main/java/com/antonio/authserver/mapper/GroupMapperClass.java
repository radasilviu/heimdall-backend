package com.antonio.authserver.mapper;

import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.dto.GroupDto;
import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.UserGroup;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class GroupMapperClass {

    private AppUserMapper appUserMapper;

    public GroupMapperClass(AppUserMapper appUserMapper) {
        this.appUserMapper = appUserMapper;
    }

    public GroupDto daoToDto(UserGroup userGroup){
        return new GroupDto(userGroup.getName(),appUserMapper.toAppUserDtoList(userGroup.getAppUserGroup()));
    }

    public List<GroupDto> daoListToDto(List<UserGroup> groups){
        List<GroupDto> groupDtos = new ArrayList<>();
        for (UserGroup group: groups){
            groupDtos.add(new GroupDto(group.getName(),appUserMapper.toAppUserDtoList(group.getAppUserGroup())));
        }
        return groupDtos;
    }

    public UserGroup dtoToDao(GroupDto groupDto){
        List<AppUser> userDtos = appUserMapper.toAppUserDaoList(groupDto.getUsers());
        return new UserGroup(groupDto.getName(),userDtos);
    }
}
