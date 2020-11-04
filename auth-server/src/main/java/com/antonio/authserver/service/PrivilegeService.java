package com.antonio.authserver.service;
import com.antonio.authserver.configuration.constants.PrivilegeType;
import com.antonio.authserver.dto.PrivilegeDto;
import com.antonio.authserver.dto.RoleDto;
import com.antonio.authserver.entity.Privilege;
import com.antonio.authserver.entity.Resource;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.entity.RoleResourcePrivilege;
import com.antonio.authserver.mapper.PrivilegeMapper;
import com.antonio.authserver.model.CustomException;
import com.antonio.authserver.repository.PrivilegeRepository;
import com.antonio.authserver.repository.ResourceRepository;
import com.antonio.authserver.repository.RoleRepository;
import com.antonio.authserver.repository.RoleResourcePrivilegeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
@Service
@Transactional
public class PrivilegeService {

    private final PrivilegeRepository privilegeRepository;
    private final PrivilegeMapper privilegeMapper;
    private final RoleRepository roleRepository;
    private final ResourceRepository resourceRepository;
    private final RoleResourcePrivilegeRepository roleResourcePrivilegeRepository;
    @Autowired
    public PrivilegeService(PrivilegeRepository privilegeRepository, PrivilegeMapper privilegeMapper, RoleRepository roleRepository, ResourceRepository resourceRepository, RoleResourcePrivilegeRepository roleResourcePrivilegeRepository) {
        this.privilegeRepository = privilegeRepository;
        this.privilegeMapper = privilegeMapper;
        this.roleRepository = roleRepository;
        this.resourceRepository = resourceRepository;
        this.roleResourcePrivilegeRepository = roleResourcePrivilegeRepository;
    }

    public List<PrivilegeDto> getAllPrivileges(){
        return privilegeMapper.toPrivilegeDtoList(privilegeRepository.findAll());
    }

    public void addPrivilegeToResourceForRole(String privilegeName, String resourceName, RoleDto role){
        Privilege privilege = getPrivilegeByNameOrThrowNotFoundException(privilegeName);
        Role foundRole = getRoleByNameAndRealmNameOrThrowNotFoundException(role.getName(),role.getRealm().getName());
        Resource resource = getResourceByNameOrThrowNotFoundException(resourceName);
        RoleResourcePrivilege roleResourcePrivilege = getRoleResourcePrivilegeByRoleAndResource(foundRole,resource);
        roleResourcePrivilege.getPrivileges().add(privilege);
        roleResourcePrivilegeRepository.save(roleResourcePrivilege);
    }

    public void removePrivilegeFromRole(String privilegeName,String resourceName, RoleDto role){
        Privilege privilege = getPrivilegeByNameOrThrowNotFoundException(privilegeName);
        Role foundRole = getRoleByNameAndRealmNameOrThrowNotFoundException(role.getName(),role.getRealm().getName());
        Resource resource = getResourceByNameOrThrowNotFoundException(resourceName);
        RoleResourcePrivilege roleResourcePrivilege = getRoleResourcePrivilegeByRoleAndResource(foundRole,resource);
        roleResourcePrivilege.getPrivileges().remove(privilege);
        roleResourcePrivilegeRepository.save(roleResourcePrivilege);
    }

    @Transactional
    public void createPrivilegeIfNotFound(String name) {

        Optional<Privilege> privilege = privilegeRepository.findByName(name);
        if(privilege.isPresent()){
            throw new CustomException("The privilege with the name [" + name + " ] already exists!",HttpStatus.CONFLICT);
        }
        else {
            Privilege newPrivilege = new Privilege(name);
            privilegeRepository.save(newPrivilege);
        }
    }

    public Set<PrivilegeDto> getPrivilegesForResource(String realmName,String roleName,String resourceName){
        Resource resource = getResourceByNameOrThrowNotFoundException(resourceName);
        Role role = getRoleByNameAndRealmNameOrThrowNotFoundException(roleName,realmName);
        RoleResourcePrivilege roleResourcePrivilege = getRoleResourcePrivilegeByRoleAndResource(role,resource);
        return privilegeMapper.toPrivilegeDtoSet(roleResourcePrivilege.getPrivileges());
    }

    public void createPrivileges(){
        createPrivilegeIfNotFound(PrivilegeType.READ.getMessage());
        createPrivilegeIfNotFound(PrivilegeType.WRITE.getMessage());
        createPrivilegeIfNotFound(PrivilegeType.EDIT.getMessage());
        createPrivilegeIfNotFound(PrivilegeType.DELETE.getMessage());
    }

    public void generatePrivileges(){
        createPrivileges();
    }
    @Transactional
    public Resource getResourceFromRole(Role role, String resourceName){
        Set<Resource> resourcesForRole = resourceRepository.getResourcesForRole(role.getName());
        boolean hasResource = false;
        Resource foundResource = resourceRepository.findByName(resourceName).orElseThrow(() -> new CustomException("The resource could not be found!", HttpStatus.NOT_FOUND));
            for (Resource resource : resourcesForRole) {
                if (resource.getName().equals(resourceName)) {
                    hasResource = true;
                    break;
                }
            }
        if(hasResource)
            return foundResource;
        else
            return new Resource("");
    }

    public Boolean checkIfUserHasPrivilegeForResource(Role role, String resourceName,String requestType){
        boolean hasPrivilege = false;
        Resource foundResource = getResourceFromRole(role,resourceName);
        RoleResourcePrivilege roleResourcePrivilege = getRoleResourcePrivilegeByRoleAndResource(role, foundResource);
        String privilegeType = getPrivilegeTypeByRequestType(requestType);
        Privilege privilege = getPrivilegeByNameOrThrowNotFoundException(privilegeType);
        for (Privilege userPrivilege : roleResourcePrivilege.getPrivileges()) {
            if (userPrivilege.getName().equals(privilege.getName())) {
                hasPrivilege=true;
                break;
            }
        }
            return hasPrivilege;
    }

    private String getPrivilegeTypeByRequestType(String requestType){
        switch (requestType) {
            case "GET":
                return PrivilegeType.READ.getMessage();
            case "POST":
                return PrivilegeType.WRITE.getMessage();
            case "PUT":
                return PrivilegeType.EDIT.getMessage();
            case "DELETE":
                return PrivilegeType.DELETE.getMessage();
            default:
                return "FAIL";
        }
    }

    private RoleResourcePrivilege getRoleResourcePrivilegeByRoleAndResource(Role role,Resource resource){
        return roleResourcePrivilegeRepository.findByRoleAndResource(role,resource).orElseThrow(() -> new CustomException("The role does not have the resource assigned to it!",HttpStatus.NOT_FOUND));
    }
    private Privilege getPrivilegeByNameOrThrowNotFoundException(String privilegeName){
        return privilegeRepository.findByName(privilegeName).orElseThrow(() -> new CustomException("The privilege with the name [" + privilegeName + "] could not be found!",HttpStatus.NOT_FOUND));
    }
    private Role getRoleByNameAndRealmNameOrThrowNotFoundException(String roleName,String realmName){
        return roleRepository.findByNameAndRealmName(roleName,realmName).orElseThrow(() -> new CustomException("The role with the name [" + roleName + "] could not be found!",HttpStatus.NOT_FOUND));
    }
    private Resource getResourceByNameOrThrowNotFoundException(String resourceName){
        return resourceRepository.findByName(resourceName).orElseThrow(() -> new CustomException("The resource with the name [" + resourceName +"] could not be found!",HttpStatus.NOT_FOUND));
    }
}
