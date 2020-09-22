package com.antonio.authserver.service;

import java.util.List;
import java.util.Optional;

import com.antonio.authserver.model.exceptions.controllerexceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.mapper.AppUserMapper;
import com.antonio.authserver.repository.AppUserRepository;
import com.antonio.authserver.repository.RoleRepository;

@Service
public class UserService {

    private AppUserRepository appUserRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder passwordEncoder;


    @Autowired
    public UserService(AppUserRepository appUserRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<AppUserDto> getAllUsers() {
        return AppUserMapper.INSTANCE.toAppUserDtoList(appUserRepository.findAll());
    }

    public AppUserDto getUserByUsername(String username) throws UserNotFound {
        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> new UserNotFound(username));
        return AppUserMapper.INSTANCE.toAppUserDto(appUser);
    }

    public void save(AppUserDto appUser) throws UserAlreadyExists, NullResource {
        appUser.setUsername(appUser.getUsername().replaceAll("\\s+", ""));
        Optional<AppUser> byUsername = appUserRepository.findByUsername(appUser.getUsername());
        if (byUsername.isPresent())
            throw new UserAlreadyExists(appUser.getUsername());
        else if (appUser.getUsername().equals("")) {
            throw new NullResource("User");
        } else {

            appUserRepository.save(AppUserMapper.INSTANCE.toAppUserDao(appUser));
        }
    }

    public void updateUserByUsername(String username, AppUserDto appUserDto) throws UserNotFound {
        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> new UserNotFound(username));
        appUser.setUsername(appUserDto.getUsername());
        appUser.setPassword(appUser.getPassword());
        appUserRepository.save(appUser);
    }

    public AppUser addRole(String username, Role role) throws UserNotFound, CannotAddRole {
        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> new UserNotFound(username));
        roleRepository.findByName(role.getName()).orElseThrow(() -> new CannotAddRole(role.getName()));

        appUser.getRoles().add(role);
        appUserRepository.save(appUser);

        return appUser;
    }

    public void removeRole(String username, Role role) throws UserNotFound, CannotAddRole {
        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> new UserNotFound(username));
        roleRepository.findByName(role.getName()).orElseThrow(() -> new CannotAddRole(role.getName()));

        appUser.getRoles().remove(role);
        appUserRepository.save(appUser);

    }

    public void deleteUser(String username) throws UserNotFound {
        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> new UserNotFound(username));
        appUserRepository.delete(appUser);

    }

    public AppUserDto findByUsernameAndPassword(String username, String password) {
        Optional<AppUser> userOptional = appUserRepository.findByUsername(username);

        if (!userOptional.isPresent()) {
            throw new UserNotFound(username);
        }
        AppUserDto userDto = AppUserMapper.INSTANCE.toAppUserDto(userOptional.get());

        if (!passwordEncoder.matches(password, userDto.getPassword())) {
            throw new IncorrectPassword(password);
        }


        return userDto;
    }

    public AppUserDto findByCode(String code) {
        final Optional<AppUser> userOptional = appUserRepository.findByCode(code);

        if (!userOptional.isPresent()) {
            throw new AccessDeniedException("Your client do not have permission to use this app");
        }

        return AppUserMapper.INSTANCE.toAppUserDto(userOptional.get());
    }

    public AppUserDto findByUsername(String username) {
        final Optional<AppUser> userOptional = appUserRepository.findByUsername(username);

        if (!userOptional.isPresent()) {
            throw new UserNotFound(username);
        }

        return AppUserMapper.INSTANCE.toAppUserDto(userOptional.get());
    }
}
