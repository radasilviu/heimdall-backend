package com.antonio.authserver.service;

import com.antonio.authserver.dto.RoleDto;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.mapper.RoleMapper;
import com.antonio.authserver.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {


    private RoleRepository roleRepository;

    private RoleMapper roleMapper;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    public void saveRole(RoleDto role) {
        roleRepository.save(roleMapper.toRoleDao(role));
    }

    public List<RoleDto> getAllRoles() {
        return roleMapper.toRoleDtoList(roleRepository.findAll());
    }

    public void deleteRoleById(Long id) {
        
        roleRepository.deleteById(id);
    }
}
