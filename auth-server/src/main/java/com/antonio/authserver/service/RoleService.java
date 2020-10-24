package com.antonio.authserver.service;
import java.util.*;

import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.entity.Realm;
import com.antonio.authserver.repository.RealmRepository;
import com.sun.org.apache.xpath.internal.operations.Bool;
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

import javax.transaction.Transactional;
@Service
@Transactional
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
		if(role.getName().equals("ROLE_ADMIN"))
			throw new CustomException("You are not allowed to create additional ADMIN roles.",HttpStatus.BAD_REQUEST);
		Optional<Role> byName = roleRepository.findByNameAndRealmName(role.getName(),realmName);
		if (byName.isPresent())
			throw new CustomException("Role with the name [ " + byName.get().getName() + " ] already exists!", HttpStatus.CONFLICT);
		else if (role.getName().equals("")) {
			throw new CustomException("The inserted Role cannot be null!", HttpStatus.BAD_REQUEST);
		} else {
			role.setRealm(realmRepository.findByName(realmName).get());
			roleRepository.save(roleMapper.toRoleDao(role));
		}
	}

	public List<RoleDto> getAllRoles(String realmName) {
		List<Role> allByRealmName = roleRepository.findAllByRealmName(realmName);
		List<Role> found = new ArrayList<Role>();
		for(Role role : allByRealmName){
			if(role.getName().equals("ROLE_ADMIN")){
				found.add(role);
			}
		}
		allByRealmName.removeAll(found);
		return roleMapper.toRoleDtoList(allByRealmName);
	}

	public RoleDto getRoleByName(String realmName,String name) throws CustomException {
		Role role = roleRepository.findByNameAndRealmName(name,realmName)
				.orElseThrow(() -> new CustomException("Role with the name [" + name + "] could not be found!",
						HttpStatus.NOT_FOUND));
		return roleMapper.toRoleDto(role);
	}

	public void updateRoleByName(String realmName,String name, RoleDto roleDto) throws CustomException {
		roleDto.setName(roleDto.getName().replaceAll("\\s+", ""));
		if(roleDto.getName().equals("ROLE_ADMIN"))
			throw new CustomException("You are not allowed to create additional ADMIN roles.",HttpStatus.BAD_REQUEST);
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

	public Role findRoleByNameDaoAndRealmName(String name,String realmName) {
		return roleRepository.findByNameAndRealmName(name,realmName)
				.orElseThrow(() -> new CustomException("Role with the name [" + name + "] could not be found!",
						HttpStatus.NOT_FOUND));
	}

	public Boolean checkIfUserHasDesiredRole(AppUserDto appUserDto,String roleName){
		boolean hasRole = false;
		Set<Role> roles = appUserDto.getRoles();
		for (Role role:roles)
			if (role.getName().equals(roleName)) {
				hasRole = true;
				break;
			}
		return hasRole;
	}
}
