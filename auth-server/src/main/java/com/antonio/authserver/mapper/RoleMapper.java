package com.antonio.authserver.mapper;

import com.antonio.authserver.dto.RoleDto;
import com.antonio.authserver.entity.Role;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring",uses = {RealmMapper.class},injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RoleMapper {

    Role toRoleDao(RoleDto roleDto);
    RoleDto toRoleDto(Role role);
    List<RoleDto> toRoleDtoList(List<Role> list);
}
