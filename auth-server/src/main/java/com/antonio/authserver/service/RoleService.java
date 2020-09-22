package com.antonio.authserver.service;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.antonio.authserver.dto.RoleDto;
import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.mapper.RoleMapper;
import com.antonio.authserver.model.exceptions.controllerexceptions.NullResource;
import com.antonio.authserver.model.exceptions.controllerexceptions.RoleAlreadyExists;
import com.antonio.authserver.model.exceptions.controllerexceptions.RoleNotFound;
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

	public void saveRole(RoleDto role) throws RoleAlreadyExists, NullResource {
		role.setName(role.getName().replaceAll("\\s+", ""));
		Optional<Role> byName = roleRepository.findByName(role.getName());
		if (byName.isPresent())
			throw new RoleAlreadyExists(role.getName());
		else if (role.getName().equals("")) {
			throw new NullResource("Role");
		} else {

			roleRepository.save(RoleMapper.INSTANCE.toRoleDao(role));
		}
	}

	public List<RoleDto> getAllRoles() {
		return RoleMapper.INSTANCE.toRoleDtoList(roleRepository.findAll());
	}

	public RoleDto getRoleByName(String name) throws RoleNotFound {
		Role role = roleRepository.findByName(name).orElseThrow(() -> new RoleNotFound(name));
		return RoleMapper.INSTANCE.toRoleDto(role);
	}

	public void updateRoleByName(String name, RoleDto roleDto) throws RoleNotFound {
		roleDto.setName(roleDto.getName().replaceAll("\\s+", ""));
		Role role = roleRepository.findByName(name).orElseThrow(() -> new RoleNotFound(name));
		if (roleDto.getName().equals(""))
			throw new NullResource("Role");
		role.setName(roleDto.getName());
		roleRepository.save(role);
	}

	public void deleteRoleByName(String name) throws RoleNotFound {
		Set<Role> roles = roleRepository.findAllByName(name);
		Optional<List<AppUser>> users = appUserRepository.findByRolesIn(roles);
		if (users.isPresent()) {
			deleteRoleFromUsers(users, roles);
		}
		roleRepository.delete(roleRepository.findByName(name).orElseThrow(() -> new RoleNotFound(name)));
	}

	private void deleteRoleFromUsers(Optional<List<AppUser>> users, Set<Role> roles) {
		for (AppUser i : users.get()) {
			Iterator iter = roles.iterator();
			if (i.getRoles().contains(iter.next())) {
				AppUser appUser = appUserRepository.findByUsername(i.getUsername()).get();
				Set<Role> rolesFromUser = appUser.getRoles();
				rolesFromUser.removeAll(roles);
				appUser.setRoles(rolesFromUser);
				appUserRepository.deleteById(appUser.getId());
				appUserRepository.save(appUser);
			}
		}
	}

}
