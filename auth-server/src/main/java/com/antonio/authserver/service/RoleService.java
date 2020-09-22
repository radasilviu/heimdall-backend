package com.antonio.authserver.service;

import com.antonio.authserver.dto.RoleDto;
import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.mapper.RoleMapper;
import com.antonio.authserver.model.exceptions.controllerexceptions.NullResource;
import com.antonio.authserver.model.exceptions.controllerexceptions.RoleAlreadyExists;
import com.antonio.authserver.model.exceptions.controllerexceptions.RoleAssignedException;
import com.antonio.authserver.model.exceptions.controllerexceptions.RoleNotFound;
import com.antonio.authserver.repository.AppUserRepository;
import com.antonio.authserver.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
		Role role = roleRepository.findByName(name).orElseThrow(() -> new RoleNotFound(name));
		role.setName(roleDto.getName());
		roleRepository.save(role);
	}

    public void deleteRoleByName(String name){
        Optional<Role> role = roleRepository.findByName(name);
        Set<Role> roles = new HashSet<>();
        roles.add(role.get());
        List<AppUser> users = appUserRepository.findAllByRolesIn(roles);
        if(users.isEmpty()){
            roleRepository.deleteByName(name);
        }
        else{
            throw new RoleAssignedException(name, users);
        }
    }

    public Role findRoleByNameDAO(String name){
		return roleRepository.findByName(name).orElseThrow(() -> new RoleNotFound("Role not found with name " + name));
	}
}
