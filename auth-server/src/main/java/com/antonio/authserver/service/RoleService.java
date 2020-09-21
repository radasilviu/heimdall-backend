package com.antonio.authserver.service;

import com.antonio.authserver.dto.RoleDto;
import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.mapper.RoleMapper;
import com.antonio.authserver.repository.AppUserRepository;
import com.antonio.authserver.repository.RoleRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    public void deleteRoleByName(String name) {
        Set<Role> roles = roleRepository.findAllByName(name);
        Optional<List<AppUser>> users = appUserRepository.findByRolesIn(roles);
        if(users.isPresent()){
            for(AppUser i : users.get()) {
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
        roleRepository.delete(roleRepository.findByName(name).orElseThrow(() -> new RuntimeException("Role not found")));
    }




}
