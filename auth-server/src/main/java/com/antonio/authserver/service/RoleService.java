package com.antonio.authserver.service;
import java.util.*;

import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.entity.Resource;
import com.antonio.authserver.entity.RoleResourcePrivilege;
import com.antonio.authserver.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.antonio.authserver.dto.RoleDto;
import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.mapper.RoleMapper;
import com.antonio.authserver.model.CustomException;

import javax.transaction.Transactional;
@Service
@Transactional
public class RoleService {

	private final RoleRepository roleRepository;
	private final AppUserRepository appUserRepository;
	private final RoleMapper roleMapper;
	private final RealmRepository realmRepository;
	private final ResourceRepository resourceRepository;
	private final RoleResourcePrivilegeRepository roleResourcePrivilegeRepository;
	@Autowired
	public RoleService(RoleRepository roleRepository, AppUserRepository appUserRepository, RoleMapper roleMapper, RealmRepository realmRepository, ResourceRepository resourceRepository, RoleResourcePrivilegeRepository roleResourcePrivilegeRepository) {
		this.roleRepository = roleRepository;
		this.appUserRepository = appUserRepository;
		this.roleMapper = roleMapper;
		this.realmRepository = realmRepository;
		this.resourceRepository = resourceRepository;
		this.roleResourcePrivilegeRepository = roleResourcePrivilegeRepository;
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
			role.setRoleResources(new HashSet<>());
			Role mappedRole = roleMapper.toRoleDao(role);
			roleRepository.save(mappedRole);
			addAllResourcesForNewRole(mappedRole);
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
		Optional<Role> duplicateRole = roleRepository.findByNameAndRealmName(roleDto.getName(), realmName);
		if (duplicateRole.isPresent())
			throw new CustomException("There already is a role with that name!",HttpStatus.CONFLICT);
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

	public void addResourceToRole(String realmName,String roleName,String resourceName){
		Role role = getRoleByRealmNameAndNameOrThrowExceptionIfNotFound(realmName, roleName);
		Resource resource = getResourceByNameOrThrowExceptionIfNotFound(resourceName);
		Set<Resource> roleResources = role.getRoleResources();
		roleResources.add(resource);
		roleRepository.save(role);
		if(getRoleResourcePrivilegeByRoleAndResource(role,resource).isPresent())
			throw new CustomException("The role [" + roleName + "] already has this resource assigned!",HttpStatus.CONFLICT);
		else{
			RoleResourcePrivilege roleResourcePrivilege = new RoleResourcePrivilege(role,resource,new HashSet<>());
			roleResourcePrivilegeRepository.save(roleResourcePrivilege);
		}
	}
	public void removeResourceFromRole(String realmName,String roleName,String resourceName){
		Role role = getRoleByRealmNameAndNameOrThrowExceptionIfNotFound(realmName, roleName);
		Resource resource = getResourceByNameOrThrowExceptionIfNotFound(resourceName);
		Set<Resource> roleResources = role.getRoleResources();
		roleResources.remove(resource);
		roleRepository.save(role);
		if(!getRoleResourcePrivilegeByRoleAndResource(role,resource).isPresent())
			throw new CustomException("The role [" + roleName + "] does not have this resource assigned!",HttpStatus.CONFLICT);
		else{
			roleResourcePrivilegeRepository.delete(getRoleResourcePrivilegeByRoleAndResource(role,resource).get());
		}
	}
	public void addAllResourcesForNewRole(Role role){
		List<Resource> all = resourceRepository.findAll();
		for(Resource resource : all){
			addResourceToRole(role.getRealm().getName(),role.getName(),resource.getName());
		}
	}
	public Boolean checkFfUserIsAdmin(AppUserDto appUserDto){
		boolean isAdmin = false;
		for (Role role : appUserDto.getRoles()) {
			if (role.getName().equals("ROLE_ADMIN")) {
				isAdmin = true;
				break;
			}
		}
		return isAdmin;
	}
	private Resource getResourceByNameOrThrowExceptionIfNotFound(String resourceName){
		return resourceRepository.findByName(resourceName).orElseThrow(() -> new CustomException("The resource with the name [" + resourceName + "] could not be found!", HttpStatus.NOT_FOUND));
	}
	private Role getRoleByRealmNameAndNameOrThrowExceptionIfNotFound(String realmName,String roleName){
		return roleRepository.findByNameAndRealmName(roleName,realmName).orElseThrow(() -> new CustomException("Role with the name [" + roleName +"] could not be found!",HttpStatus.NOT_FOUND));
	}
	private Optional<RoleResourcePrivilege> getRoleResourcePrivilegeByRoleAndResource(Role role,Resource resource){
		return roleResourcePrivilegeRepository.findByRoleAndResource(role, resource);
	}
}
