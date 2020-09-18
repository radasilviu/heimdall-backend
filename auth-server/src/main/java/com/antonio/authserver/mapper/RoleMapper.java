package com.antonio.authserver.mapper;

import com.antonio.authserver.dto.RoleDto;
import com.antonio.authserver.entity.Role;

import java.util.List;
import java.util.stream.Collectors;

public class RoleMapper {

    public Role toRoleDao(RoleDto roleDto){
        return new Role(roleDto.getName());
    }
    public RoleDto toRoleDto(Role role){
        return new RoleDto(role.getName());
    }
    public List<RoleDto> toRoleDtoList(List<Role> list){
        return list.stream().map(role -> new RoleDto(role.getName()))
                .collect(Collectors.toList());
    }
}
