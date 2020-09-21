package com.antonio.authserver.service;

import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.mapper.AppUserMapper;
import com.antonio.authserver.model.exceptions.AuthorizationServerError;
import com.antonio.authserver.repository.AppUserRepository;
import com.antonio.authserver.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
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
        if (!checkIfUserExist(username)) {
            return ResponseEntity.badRequest().body(new AuthorizationServerError(HttpStatus.BAD_REQUEST, LocalDateTime.now(), "User with username: " + username + "doesn't exist"));
        }

        if (!checkIfRoleExist(role.getName())) {
            return ResponseEntity.badRequest().body(new AuthorizationServerError(HttpStatus.BAD_REQUEST, LocalDateTime.now(), "Role  with the name: " + role.getName() + "can not be added to user, it need to be saved firstly"));
        }

        AppUser user = appUserRepository.findByUsername(username).get();
        user.getRoles().add(role);
        appUserRepository.save(user);

        return ResponseEntity.ok().body(user);
    }

    public void removeRole(String username, Role role) {
        if (!checkIfUserExist(username)) {
            throw new RuntimeException("User with username: " + username + "doesn't exist");
        }

        if (!checkIfRoleExist(role.getName())) {
            throw new RuntimeException("Role  with the name: " + role.getName() + "can not be added to user, it need to be saved firstly");
        }

        AppUser user = appUserRepository.findByUsername(username).get();
        user.getRoles().remove(role);
        appUserRepository.save(user);


    }


    public AppUserDto getUserByUsername(String username) {

        Optional<AppUser> userOptional = appUserRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("Incorrect username: " + username);
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
        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("no user found"));
        appUserRepository.delete(appUser);

    }
}
