package com.antonio.authserver.service;

import com.antonio.authserver.dto.RoleDto;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.mapper.RoleMapper;
import com.antonio.authserver.model.exceptions.controllerexceptions.RoleAlreadyExists;
import com.antonio.authserver.model.exceptions.controllerexceptions.RoleNotFound;
import com.antonio.authserver.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {


    private RoleRepository roleRepository;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    public void saveRole(RoleDto role) throws RoleAlreadyExists {
        if (roleRepository.findByName(role.getName()).isPresent())
            throw new RoleAlreadyExists(role.getName());
        else {
            roleRepository.save(roleMapper.toRoleDao(role));
        }
    }

    public List<RoleDto> getAllRoles() {
        return roleMapper.toRoleDtoList(roleRepository.findAll());
    }

    public void deleteRoleByName(String name) throws RoleNotFound {

        Role role = roleRepository.findByName(name).orElseThrow(() -> new RoleNotFound(name));
        roleRepository.delete(role);
    }
}
