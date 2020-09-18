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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class UserService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    private AppUserMapper appUserMapper;


    public void deleteUser(AppUser appUser) {

        if (!checkIfUserExist(appUser.getId())) {
            throw new RuntimeException("User with id: " + appUser.getId() + "doesn't exist");
        }

        appUserRepository.delete(appUser);

    }


    public void save(AppUserDto appUser) {
        appUserRepository.save(appUserMapper.toAppUserDao(appUser));
    }


    public ResponseEntity<?> addRole(Long userId, Role role) {
        if (!checkIfUserExist(userId)) {
            return ResponseEntity.badRequest().body(new AuthorizationServerError(HttpStatus.BAD_REQUEST, LocalDateTime.now(), "User with id: " + userId + "doesn't exist"));
        }

        if (!checkIfRoleExist(role.getId())) {
            return ResponseEntity.badRequest().body(new AuthorizationServerError(HttpStatus.BAD_REQUEST, LocalDateTime.now(), "Role  with id: " + role.getId() + "can not be added to user, it need to be saved firstly"));
        }

        AppUser user = appUserRepository.getOne(userId);
        user.getRoles().add(role);
        appUserRepository.save(user);

        return ResponseEntity.ok().body(user);
    }

    public void removeRole(Long userId, Role role) {
        if (!checkIfUserExist(userId)) {
            throw new RuntimeException("User with id: " + userId + "doesn't exist");
        }

        if (!checkIfRoleExist(role.getId())) {
            throw new RuntimeException("Role  with id: " + role.getId() + "can not be added to user, it need to be saved firstly");
        }

        AppUser user = appUserRepository.getOne(userId);
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

    private boolean checkIfRoleExist(Long roleId) {
        Optional<Role> roleOptional = roleRepository.findById(roleId);

        return roleOptional.isPresent();

    }

    private boolean checkIfUserExist(Long userId) {
        Optional<AppUser> userOptional = appUserRepository.findById(userId);

        return userOptional.isPresent();

    }
}
