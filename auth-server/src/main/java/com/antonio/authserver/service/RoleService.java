package com.antonio.authserver.service;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.antonio.authserver.entity.Realm;
import com.antonio.authserver.repository.RealmRepository;
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
	private RoleMapper roleMapper;
	private RealmRepository realmRepository;

	@Autowired
	public RoleService(RoleRepository roleRepository, AppUserRepository appUserRepository, RoleMapper roleMapper, RealmRepository realmRepository) {
		this.roleRepository = roleRepository;
		this.appUserRepository = appUserRepository;
		this.roleMapper = roleMapper;
		this.realmRepository = realmRepository;
	}

	public void saveRole(String realmName,RoleDto role) throws CustomException {
		role.setName(role.getName().replaceAll("\\s+", ""));
		Optional<Role> byName = roleRepository.findByNameAndRealmName(role.getName(),realmName);
		if (byName.isPresent())
			throw new CustomException("Role with the name [ " + byName.get().getName() + " ] already exists!",
					HttpStatus.CONFLICT);
		else if (role.getName().equals("")) {
			throw new CustomException("The inserted Role cannot be null!", HttpStatus.BAD_REQUEST);
		} else {
			role.setRealm(realmRepository.findByName(realmName).get());
			roleRepository.save(roleMapper.toRoleDao(role));
		}
	}

	public List<RoleDto> getAllRoles(String realmName) {
		return roleMapper.toRoleDtoList(roleRepository.findAllByRealmName(realmName));
	}

	public RoleDto getRoleByName(String realmName,String name) throws CustomException {
		Role role = roleRepository.findByNameAndRealmName(name,realmName)
				.orElseThrow(() -> new CustomException("Role with the name [" + name + "] could not be found!",
						HttpStatus.NOT_FOUND));
		return roleMapper.toRoleDto(role);
	}

	public void updateRoleByName(String realmName,String name, RoleDto roleDto) throws CustomException {
		roleDto.setName(roleDto.getName().replaceAll("\\s+", ""));
		Role role = roleRepository.findByNameAndRealmName(name,realmName)
				.orElseThrow(() -> new CustomException("Role with the name [" + name + "] could not be found!",
						HttpStatus.NOT_FOUND));
		if (roleDto.getName().equals(""))
			throw new CustomException("The inserted Role cannot be null!", HttpStatus.BAD_REQUEST);
		role.setName(roleDto.getName());
		roleRepository.save(role);
	}

	public void deleteRoleByName(String realmName,String name) {
		Optional<Role> role = roleRepository.findByNameAndRealmName(name,realmName);
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
