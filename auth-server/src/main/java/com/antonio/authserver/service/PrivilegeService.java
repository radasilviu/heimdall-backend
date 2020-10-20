package com.antonio.authserver.service;
import com.antonio.authserver.dto.PrivilegeDto;
import com.antonio.authserver.dto.RoleDto;
import com.antonio.authserver.entity.Privilege;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.mapper.PrivilegeMapper;
import com.antonio.authserver.model.CustomException;
import com.antonio.authserver.repository.PrivilegeRepository;
import com.antonio.authserver.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
@Service
public class PrivilegeService {

    private final PrivilegeRepository privilegeRepository;
    private final PrivilegeMapper privilegeMapper;
    private final RoleRepository roleRepository;
    private final RoleService roleService;
    @Autowired
    public PrivilegeService(PrivilegeRepository privilegeRepository, PrivilegeMapper privilegeMapper, RoleRepository roleRepository, RoleService roleService) {
        this.privilegeRepository = privilegeRepository;
        this.privilegeMapper = privilegeMapper;
        this.roleRepository = roleRepository;
        this.roleService = roleService;
    }

    public List<PrivilegeDto> getAllPrivileges(){
        return privilegeMapper.toPrivilegeDtoList(privilegeRepository.findAll());
    }
    public PrivilegeDto getPrivilegeByName(String name){
        Privilege privilege = privilegeRepository.findByName(name).orElseThrow(() -> new CustomException("The privilege with the name [" + name + " ] could not be found!", HttpStatus.NOT_FOUND));
        return privilegeMapper.toPrivilegeDto(privilege);
    }
    public void addPrivilegeToRole(String name, RoleDto roleDto){
        Privilege privilege = privilegeRepository.findByName(name).orElseThrow(() -> new CustomException("The privilege with the name [" + name + " ] could not be found!", HttpStatus.NOT_FOUND));
        Role role = roleRepository.findByNameAndRealmName(roleDto.getName(), roleDto.getRealm().getName()).orElseThrow(() -> new CustomException("The role with the name [" + roleDto.getName() + "] could not be found!", HttpStatus.NOT_FOUND));
        Set<Privilege> privileges = role.getPrivileges();
        privileges.add(privilege);
        role.setPrivileges(privileges);
        roleRepository.save(role);
    }
    public void removePrivilegeFromRole(String name,RoleDto roleDto){
        Privilege privilege = privilegeRepository.findByName(name).orElseThrow(() -> new CustomException("The privilege with the name [" + name + " ] could not be found!", HttpStatus.NOT_FOUND));
        Role role = roleRepository.findByNameAndRealmName(roleDto.getName(), roleDto.getRealm().getName()).orElseThrow(() -> new CustomException("The role with the name [" + roleDto.getName() + "] could not be found!", HttpStatus.NOT_FOUND));
        Set<Privilege> privileges = role.getPrivileges();
        privileges.remove(privilege);
        role.setPrivileges(privileges);
        roleRepository.save(role);
    }
    @Transactional
    public Privilege createPrivilegeIfNotFound(String name,String resourceName) {

        Optional<Privilege> privilege = privilegeRepository.findByNameAndResource(name,resourceName);
        if(privilege.isPresent()){
            throw new CustomException("The privilege with the name [" + name + " ] already exists!",HttpStatus.CONFLICT);
        }
        else {
            Privilege privilege1 = new Privilege(resourceName + name,resourceName,null);
            privilegeRepository.save(privilege1);
            return privilege1;
        }
    }

    public Set<PrivilegeDto> getPrivilegesForRole(String realmName,String roleName){
        RoleDto roleByName = roleService.getRoleByName(realmName, roleName);
        return roleByName.getPrivileges();
    }
    public void createPrivileges(String resourceName){
        createPrivilegeIfNotFound("_READ_PRIVILEGE",resourceName);
        createPrivilegeIfNotFound("_WRITE_PRIVILEGE",resourceName);
        createPrivilegeIfNotFound("_EDIT_PRIVILEGE",resourceName);
        createPrivilegeIfNotFound("_DELETE_PRIVILEGE",resourceName);
    }
    public void createBookAndCompanyPrivileges(){
        createPrivileges("BOOK");
        createPrivileges("COMPANY");
    }
}
