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
        appUser.setId(appUserDto.getId());
        appUser.setCode(appUserDto.getCode());
        appUser.setToken(appUserDto.getToken());
        appUser.setUsername(appUserDto.getUsername());
        appUser.setPassword(appUserDto.getPassword());
        appUser.getRoles().addAll(appUserDto.getRoles());
        return appUser;
    }

    public AppUserDto toAppUserDto(AppUser appUser) {
        AppUserDto appUserDto = new AppUserDto();
        appUserDto.setId(appUser.getId());
        appUserDto.setCode(appUser.getCode());
        appUserDto.setToken(appUser.getToken());
        appUserDto.setUsername(appUser.getUsername());
        appUserDto.setPassword(appUser.getPassword());
        appUserDto.getRoles().addAll(appUser.getRoles());
        return appUserDto;
    }

    public List<AppUserDto> toAppUserDtoList(List<AppUser> list) {
        return list.stream().map(appUser -> new AppUserDto(appUser.getUsername(), appUser.getUsername(), appUser.getCode(), appUser.getToken(), appUser.getRoles()))
                .collect(Collectors.toList());
    }
}
