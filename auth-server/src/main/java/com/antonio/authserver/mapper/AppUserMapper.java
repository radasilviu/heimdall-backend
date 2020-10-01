package com.antonio.authserver.mapper;

import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.entity.AppUser;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RoleMapper.class, IdentityProviderMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AppUserMapper {


    AppUser toAppUserDao(AppUserDto appUserDto);

    AppUserDto toAppUserDto(AppUser appUser);

    List<AppUserDto> toAppUserDtoList(List<AppUser> list);
}
