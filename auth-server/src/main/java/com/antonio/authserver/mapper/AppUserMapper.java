package com.antonio.authserver.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.entity.AppUser;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface AppUserMapper {

    AppUserMapper INSTANCE = Mappers.getMapper(AppUserMapper.class);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "code", target = "code")
    @Mapping(source = "token", target = "token")
    @Mapping(source = "roles", target = "roles")
    AppUser toAppUserDao(AppUserDto appUserDto);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "code", target = "code")
    @Mapping(source = "token", target = "token")
    @Mapping(source = "roles", target = "roles")
    AppUserDto toAppUserDto(AppUser appUser);

    List<AppUserDto> toAppUserDtoList(List<AppUser> list);
}
