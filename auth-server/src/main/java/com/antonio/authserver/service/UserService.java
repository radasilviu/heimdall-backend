package com.antonio.authserver.service;

import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.mapper.AppUserMapper;
import com.antonio.authserver.model.exceptions.controllerexceptions.IncorrectPassword;
import com.antonio.authserver.model.exceptions.controllerexceptions.RoleNotFound;
import com.antonio.authserver.model.exceptions.controllerexceptions.UserNotFound;
import com.antonio.authserver.repository.AppUserRepository;
import com.antonio.authserver.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class UserService {


    private AppUserRepository appUserRepository;
    private RoleRepository roleRepository;
    private AppUserMapper appUserMapper;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(AppUserRepository appUserRepository, RoleRepository roleRepository, AppUserMapper appUserMapper, BCryptPasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.roleRepository = roleRepository;
        this.appUserMapper = appUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public void save(AppUserDto appUserDTO) {
        AppUser appUser = appUserMapper.toAppUserDao(appUserDTO);
        appUser.setPassword(passwordEncoder.encode(appUserDTO.getPassword()));
        appUserRepository.save(appUser);
    }


    public ResponseEntity<?> addRole(String username, Role role) {
        checkIfUserExist(username);
        checkIfRoleExist(role.getName());

        AppUser user = appUserRepository.findByUsername(username).get();
        user.getRoles().add(role);
        appUserRepository.save(user);

        return ResponseEntity.ok().body(user);
    }

    public void removeRole(String username, Role role) {
        if (!checkIfUserExist(username)) {
            throw new UserNotFound(username);
        }

        if (!checkIfRoleExist(role.getName())) {
            throw new RoleNotFound(role.getName());
        }

        AppUser user = appUserRepository.findByUsername(username).get();
        user.getRoles().remove(role);
        appUserRepository.save(user);


    }


    public AppUserDto getUserByUsername(String username) {

        Optional<AppUser> userOptional = appUserRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            throw new UserNotFound(username);
        }

        return appUserMapper.toAppUserDto(userOptional.get());
    }

    public List<AppUserDto> getAllUsers() {
        return appUserMapper.toAppUserDtoList(appUserRepository.findAll());
    }

    private boolean checkIfRoleExist(String name) {
        Optional<Role> roleOptional = roleRepository.findByName(name);

        return roleOptional.isPresent();

    }

    private boolean checkIfUserExist(String username) {
        Optional<AppUser> userOptional = appUserRepository.findByUsername(username);

        return userOptional.isPresent();

    }

    public void deleteUser(String username) {
        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> new UserNotFound(username));
        appUserRepository.delete(appUser);

    }

    public AppUserDto findByUsernameAndPassword(String username, String password) {
        Optional<AppUser> userOptional = appUserRepository.findByUsername(username);

        if (!userOptional.isPresent()) {
            throw new UserNotFound(username);
        }
        AppUserDto userDto = appUserMapper.toAppUserDto(userOptional.get());

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

        return appUserMapper.toAppUserDto(userOptional.get());
    }

    public AppUserDto findByUsername(String username) {
        final Optional<AppUser> userOptional = appUserRepository.findByUsername(username);

        if (!userOptional.isPresent()) {
            throw new UserNotFound(username);
        }

        return appUserMapper.toAppUserDto(userOptional.get());
    }
}
