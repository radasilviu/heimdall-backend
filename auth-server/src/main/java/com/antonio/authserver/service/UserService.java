package com.antonio.authserver.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.mapper.AppUserMapper;
import com.antonio.authserver.model.exceptions.controllerexceptions.CannotAddRole;
import com.antonio.authserver.model.exceptions.controllerexceptions.UserAlreadyExists;
import com.antonio.authserver.model.exceptions.controllerexceptions.UserNotFound;
import com.antonio.authserver.repository.AppUserRepository;
import com.antonio.authserver.repository.RoleRepository;

@Service
public class UserService {

	@Autowired
	private AppUserRepository appUserRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private AppUserMapper appUserMapper;

	public void save(AppUserDto appUser) throws UserAlreadyExists {
		if (appUserRepository.findByUsername(appUser.getUsername()).isPresent())
			throw new UserAlreadyExists(appUser.getUsername());
		else {
			appUserRepository.save(appUserMapper.toAppUserDao(appUser));
		}
	}

	public ResponseEntity<?> addRole(String username, Role role) throws UserNotFound, CannotAddRole {
		AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> new UserNotFound(username));
		roleRepository.findByName(role.getName()).orElseThrow(() -> new CannotAddRole(role.getName()));

		appUser.getRoles().add(role);
		appUserRepository.save(appUser);

		return ResponseEntity.ok().body(appUser); // IS THIS NEEDED?
	}

	public void removeRole(String username, Role role) throws UserNotFound, CannotAddRole {
		AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> new UserNotFound(username));
		roleRepository.findByName(role.getName()).orElseThrow(() -> new CannotAddRole(role.getName()));

		appUser.getRoles().remove(role);
		appUserRepository.save(appUser);

	}

	public AppUserDto getUserByUsername(String username) throws UserNotFound {
		AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> new UserNotFound(username));
		return appUserMapper.toAppUserDto(appUser);
	}

	public List<AppUserDto> getAllUsers() {
		return appUserMapper.toAppUserDtoList(appUserRepository.findAll());
	}

	public void deleteUser(String username) throws UserNotFound {
		AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> new UserNotFound(username));
		appUserRepository.delete(appUser);

	}
}
