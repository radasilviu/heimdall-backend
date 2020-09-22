package com.antonio.authserver.service;

import com.antonio.authserver.dto.RoleDto;
import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.mapper.RoleMapper;
import com.antonio.authserver.model.exceptions.RoleAssignedException;
import com.antonio.authserver.repository.AppUserRepository;
import com.antonio.authserver.repository.RoleRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoleService {


    private RoleRepository roleRepository;
    private AppUserRepository appUserRepository;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    public RoleService(RoleRepository roleRepository, AppUserRepository appUserRepository) {
        this.roleRepository = roleRepository;
        this.appUserRepository = appUserRepository;
    }


    public void saveRole(RoleDto role) {
        roleRepository.save(roleMapper.toRoleDao(role));
    }

    public List<RoleDto> getAllRoles() {
        return roleMapper.toRoleDtoList(roleRepository.findAll());
    }

    public String deleteRoleByName(String name) {
        Optional<Role> role = roleRepository.findByName(name);
        Set<Role> roles = new HashSet<>();
        roles.add(role.get());
        List<AppUser> usersWithRole = new ArrayList<>();
        if (appUserRepository.findByRolesIn(roles).isPresent()) {
            usersWithRole = appUserRepository.findByRolesIn(roles).get();
        } else {
            throw new RuntimeException("dsdaf");
        }
        if (usersWithRole.isEmpty()) {
            roleRepository.deleteByName(name);
            return "Delete succesfully";
        } else {
            return "Blabla";
        }

    }
}
