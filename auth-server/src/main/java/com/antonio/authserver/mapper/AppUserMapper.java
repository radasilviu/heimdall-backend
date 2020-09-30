package com.antonio.authserver.mapper;

import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.entity.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RoleMapper.class, IdentityProviderMapper.class})
public interface AppUserMapper {

    AppUserMapper INSTANCE = Mappers.getMapper(AppUserMapper.class);

    @Mapping(target = "identityProvider",ignore = true)
    AppUser toAppUserDao(AppUserDto appUserDto);

    @Mapping(target = "identityProvider",ignore = true)
    AppUserDto toAppUserDto(AppUser appUser);

    List<AppUserDto> toAppUserDtoList(List<AppUser> list);
}
