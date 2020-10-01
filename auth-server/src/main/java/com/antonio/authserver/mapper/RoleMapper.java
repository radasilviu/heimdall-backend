package com.antonio.authserver.mapper;

import java.util.List;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import com.antonio.authserver.dto.RoleDto;
import com.antonio.authserver.entity.Role;

@Mapper(componentModel = "spring",injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    Role toRoleDao(RoleDto roleDto);
    RoleDto toRoleDto(Role role);
    List<RoleDto> toRoleDtoList(List<Role> list);
}
