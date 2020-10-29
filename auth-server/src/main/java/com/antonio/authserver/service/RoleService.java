package com.antonio.authserver.service;
import java.util.*;

import com.antonio.authserver.dto.ResourceDto;
import com.antonio.authserver.entity.Resource;
import com.antonio.authserver.mapper.ResourceMapper;
import com.antonio.authserver.repository.RealmRepository;
import com.antonio.authserver.repository.ResourceRepository;
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

	private final RoleRepository roleRepository;
	private final AppUserRepository appUserRepository;
	private final RoleMapper roleMapper;
	private final RealmRepository realmRepository;
	private final ResourceRepository resourceRepository;
	private final ResourceMapper resourceMapper;

	@Autowired
	public RoleService(RoleRepository roleRepository, AppUserRepository appUserRepository, RoleMapper roleMapper, RealmRepository realmRepository, ResourceRepository resourceRepository, ResourceMapper resourceMapper) {
		this.roleRepository = roleRepository;
		this.appUserRepository = appUserRepository;
		this.roleMapper = roleMapper;
		this.realmRepository = realmRepository;
		this.resourceRepository = resourceRepository;
		this.resourceMapper = resourceMapper;
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
			generateAndAddResourcesForNewRole(mappedRole); //generates and assigns resources to new role
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

	public void addResourceToRole(String realmName, String roleName, String resourceName) {
		Role role = findRoleByNameDaoAndRealmName(roleName, realmName);
		Resource resource = resourceRepository.findByNameAndRoleNameAndRealmName(resourceName, roleName, realmName).orElseThrow(() -> new CustomException("Resource with the name [" + resourceName + "] could not be found!", HttpStatus.NOT_FOUND));
		Set<Resource> roleResources = role.getRoleResources();
		roleResources.add(resource);
		roleRepository.save(role);
	}
	public void removeResourceFromRole(String realmName, String roleName, String resourceName) {
		Role role = findRoleByNameDaoAndRealmName(roleName, realmName);
		Resource resource = resourceRepository.findByNameAndRoleNameAndRealmName(resourceName, roleName, realmName).orElseThrow(() -> new CustomException("Resource with the name [" + resourceName + "] could not be found!", HttpStatus.NOT_FOUND));
		Set<Resource> roleResources = role.getRoleResources();
		roleResources.remove(resource);
		roleRepository.save(role);
	}
	public Set<ResourceDto> getResourceDtosFromDatabaseForNewRole(){
		List<Resource> all = resourceRepository.findAll();
		Set<String> uniqueResourceNames = new HashSet<>();
		for (Resource resource : all) {
			uniqueResourceNames.add(resource.getName());
		}
		Set<ResourceDto> finalResources = new HashSet<>();
		for (String resourceName : uniqueResourceNames){
			finalResources.add(new ResourceDto(resourceName,new HashSet<>()));
		}
		return finalResources;
	}

	public void generateAndAddResourcesForNewRole(Role role){
		for (ResourceDto resourceDto : getResourceDtosFromDatabaseForNewRole()){
			createResourceForRole(resourceDto,role);
			addResourceToRole(role.getRealm().getName(),role.getName(),resourceDto.getName());
		}
	}
	public void createResourceForRole(ResourceDto resourceDto, Role role) {
		Optional<Resource> byName = resourceRepository.findByNameAndRoleNameAndRealmName(resourceDto.getName(), role.getName(), role.getRealm().getName());
		if (!byName.isPresent()) {
			Resource resource = resourceMapper.toResourceDao(resourceDto);
			resource.setRoleName(role.getName());
			resource.setRealmName(role.getRealm().getName());
			resourceRepository.save(resource);
		}
	}
	public void createResourceForAllRoles(ResourceDto resourceDto) {
		List<Role> all = roleRepository.findAll();
		for (Role role : all) {
			if (!role.getName().equals("ROLE_ADMIN")) {
				createResourceForRole(resourceDto,role);
			}
		}
	}

	public List<ResourceDto> getAssignedResourcesForRole(String realmName, String roleName){
		Role role = findRoleByNameDaoAndRealmName(roleName, realmName);
		Set<Resource> roleResources = role.getRoleResources();
		List<Resource> resources = new ArrayList<>(roleResources);
		return resourceMapper.toResourceDtoList(resources);
	}

}
