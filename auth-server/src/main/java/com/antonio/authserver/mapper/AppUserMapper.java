package com.antonio.authserver.mapper;

import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.entity.AppUser;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RoleMapper.class},injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AppUserMapper {

	AppUserMapper INSTANCE = Mappers.getMapper(AppUserMapper.class);
	AppUser toAppUserDao(AppUserDto appUserDto);
	AppUserDto toAppUserDto(AppUser appUser);
	List<AppUserDto> toAppUserDtoList(List<AppUser> list);
}
