package com.antonio.authserver.mapper;

import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.entity.AppUser;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface AppUserMapper {

	AppUserMapper INSTANCE = Mappers.getMapper(AppUserMapper.class);

	@Mapping(source = "username", target = "username")
	@Mapping(source = "password", target = "password")
	@Mapping(source = "code", target = "code")
	@Mapping(source = "token", target = "token")
	@Mapping(source = "refreshToken", target = "refreshToken")
	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "email", target = "email")
	@Mapping(source = "isActivated", target = "isActivated")
	@Mapping(source = "emailCode", target = "emailCode")
	AppUser toAppUserDao(AppUserDto appUserDto);

	@Mapping(source = "username", target = "username")
	@Mapping(source = "password", target = "password")
	@Mapping(source = "code", target = "code")
	@Mapping(source = "token", target = "token")
	@Mapping(source = "refreshToken", target = "refreshToken")
	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "email", target = "email")
	@Mapping(source = "isActivated", target = "isActivated")
	@Mapping(source = "emailCode", target = "emailCode")
	AppUserDto toAppUserDto(AppUser appUser);

	List<AppUserDto> toAppUserDtoList(List<AppUser> list);
}
