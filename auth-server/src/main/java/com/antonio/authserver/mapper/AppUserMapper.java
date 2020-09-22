package com.antonio.authserver.mapper;

import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.entity.AppUser;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AppUserMapper {

    public AppUser toAppUserDao(AppUserDto appUserDto) {
        AppUser appUser = new AppUser();
        appUser.setToken(appUserDto.getToken());
        appUser.setUsername(appUserDto.getUsername());
        appUser.setPassword(appUserDto.getPassword());
        appUser.setRoles(appUserDto.getRoles());
        return appUser;
    }

    public AppUserDto toAppUserDto(AppUser appUser) {
        AppUserDto appUserDto = new AppUserDto();
        appUserDto.setToken(appUser.getToken());
        appUserDto.setUsername(appUser.getUsername());
        appUserDto.setPassword(appUser.getPassword());
        appUserDto.setRoles(appUser.getRoles());
        return appUserDto;
    }

    public List<AppUserDto> toAppUserDtoList(List<AppUser> list) {
        return list.stream().map(appUser -> new AppUserDto(appUser.getUsername(), appUser.getUsername(), appUser.getToken(), appUser.getRoles()))
                .collect(Collectors.toList());
    }
}
