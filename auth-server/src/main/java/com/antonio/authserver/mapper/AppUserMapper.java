package com.antonio.authserver.mapper;

import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.entity.AppUser;

import java.util.List;
import java.util.stream.Collectors;

public class AppUserMapper {

    public AppUser toAppUserDao(AppUserDto appUserDto){
        AppUser appUser = new AppUser();
        appUser.setUsername(appUserDto.getUsername());
        appUser.setPassword(appUserDto.getPassword());
        appUser.setRoles(appUserDto.getRoles());
        return appUser;
    }
    public AppUserDto toAppUserDto(AppUser appUser){
        return new AppUserDto(appUser.getUsername(), appUser.getPassword(), appUser.getRoles());
    }
    public List<AppUserDto> toAppUserDtoList(List<AppUser> list){
        return list.stream().map(appUser -> new AppUserDto(appUser.getUsername(), appUser.getUsername(), appUser.getRoles()))
                .collect(Collectors.toList());
    }
}
