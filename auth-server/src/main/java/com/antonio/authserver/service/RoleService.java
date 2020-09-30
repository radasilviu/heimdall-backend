package com.antonio.authserver.service;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.antonio.authserver.dto.RoleDto;
import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.mapper.RoleMapper;
import com.antonio.authserver.model.CustomException;
import com.antonio.authserver.repository.AppUserRepository;
import com.antonio.authserver.repository.RoleRepository;

@Service
public class RoleService {

	private RoleRepository roleRepository;
	private AppUserRepository appUserRepository;

	@Autowired
	public RoleService(RoleRepository roleRepository, AppUserRepository appUserRepository) {
		this.roleRepository = roleRepository;
		this.appUserRepository = appUserRepository;
	}

	public void saveRole(RoleDto role) throws CustomException {
		role.setName(role.getName().replaceAll("\\s+", ""));
		Optional<Role> byName = roleRepository.findByName(role.getName());
		if (byName.isPresent())
			throw new CustomException("Role with the name [ " + byName.get().getName() + " ] already exists!",
					HttpStatus.CONFLICT);
		else if (role.getName().equals("")) {
			throw new CustomException("The inserted Role cannot be null!", HttpStatus.BAD_REQUEST);
		} else {

			roleRepository.save(RoleMapper.INSTANCE.toRoleDao(role));
		}
	}

	public List<RoleDto> getAllRoles() {
		return RoleMapper.INSTANCE.toRoleDtoList(roleRepository.findAll());
	}

	public RoleDto getRoleByName(String name) throws CustomException {
		Role role = roleRepository.findByName(name)
				.orElseThrow(() -> new CustomException("Role with the name [" + name + "] could not be found!",
						HttpStatus.NOT_FOUND));
		return RoleMapper.INSTANCE.toRoleDto(role);
	}

	public void updateRoleByName(String name, RoleDto roleDto) throws CustomException {
		roleDto.setName(roleDto.getName().replaceAll("\\s+", ""));
		Role role = roleRepository.findByName(name)
				.orElseThrow(() -> new CustomException("Role with the name [" + name + "] could not be found!",
						HttpStatus.NOT_FOUND));
		if (roleDto.getName().equals(""))
			throw new CustomException("The inserted Role cannot be null!", HttpStatus.BAD_REQUEST);
		role.setName(roleDto.getName());
		roleRepository.save(role);
	}

	public void deleteRoleByName(String name) {
		Optional<Role> role = roleRepository.findByName(name);
		Set<Role> roles = new HashSet<>();
		roles.add(role.get());
		List<AppUser> users = appUserRepository.findAllByRolesIn(roles);
		if (users.isEmpty()) {
			roleRepository.deleteByName(name);
		} else {
			throw new CustomException("Please make sure that all users don't have the role [ " + name
					+ " ] assigned to it \n" + "Users that might have the roles are: [ " + users + " ]",
					HttpStatus.BAD_REQUEST);
		}
	}

	public Role findRoleByNameDAO(String name) {
		return roleRepository.findByName(name)
				.orElseThrow(() -> new CustomException("Role with the name [" + name + "] could not be found!",
						HttpStatus.NOT_FOUND));
	}
}
