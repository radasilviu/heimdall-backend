package com.antonio.authserver.mapper;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import com.antonio.authserver.dto.RoleDto;
import com.antonio.authserver.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {

	RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

	@Mapping(source = "name", target = "name")
	Role toRoleDao(RoleDto roleDto);
	@Mapping(source = "name", target = "name")
	RoleDto toRoleDto(Role role);
	List<RoleDto> toRoleDtoList(List<Role> list);
}
