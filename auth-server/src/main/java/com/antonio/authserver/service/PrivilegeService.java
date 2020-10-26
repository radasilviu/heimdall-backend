package com.antonio.authserver.service;
import com.antonio.authserver.configuration.constants.PrivilegeType;
import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.dto.PrivilegeDto;
import com.antonio.authserver.dto.RoleDto;
import com.antonio.authserver.entity.Privilege;
import com.antonio.authserver.entity.Resource;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.mapper.PrivilegeMapper;
import com.antonio.authserver.mapper.RoleMapper;
import com.antonio.authserver.model.CustomException;
import com.antonio.authserver.repository.PrivilegeRepository;
import com.antonio.authserver.repository.ResourceRepository;
import com.antonio.authserver.repository.RoleRepository;
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
    private final ResourceService resourceService;
    private final ResourceRepository resourceRepository;
    private final RoleMapper roleMapper;
    @Autowired
    public PrivilegeService(PrivilegeRepository privilegeRepository, PrivilegeMapper privilegeMapper, RoleRepository roleRepository, ResourceService resourceService, ResourceRepository resourceRepository, RoleMapper roleMapper) {
        this.privilegeRepository = privilegeRepository;
        this.privilegeMapper = privilegeMapper;
        this.roleRepository = roleRepository;
        this.resourceService = resourceService;
        this.resourceRepository = resourceRepository;
        this.roleMapper = roleMapper;
    }

    public List<PrivilegeDto> getAllPrivileges(){
        return privilegeMapper.toPrivilegeDtoList(privilegeRepository.findAll());
    }
    public PrivilegeDto getPrivilegeByName(String name){
        Privilege privilege = privilegeRepository.findByName(name).orElseThrow(() -> new CustomException("The privilege with the name [" + name + " ] could not be found!", HttpStatus.NOT_FOUND));
        return privilegeMapper.toPrivilegeDto(privilege);
    }
    public void addPrivilegeToResourceForRole(String privilegeName, String resourceName, RoleDto role){
        Privilege privilege = privilegeRepository.findByName(privilegeName).orElseThrow(() -> new CustomException("The privilege with the name [" + privilegeName + " ] could not be found!", HttpStatus.NOT_FOUND));
        Optional<Role> byNameAndRealmName = roleRepository.findByNameAndRealmName(role.getName(), role.getRealm().getName());
        Set<Resource> roleResources = byNameAndRealmName.get().getRoleResources();
        for(Resource resource : roleResources){
            if(resource.getName().equals(resourceName)) {
                resource.getPrivileges().add(privilege);
                resourceRepository.save(resource); // Need?
            }
            else
            {
                throw new CustomException("Resource could not be found!",HttpStatus.NOT_FOUND);
            }
        }
    }

    public void removePrivilegeFromRole(String privilegeName,String resourceName, RoleDto role){
        Privilege privilege = privilegeRepository.findByName(privilegeName).orElseThrow(() -> new CustomException("The privilege with the name [" + privilegeName + " ] could not be found!", HttpStatus.NOT_FOUND));
        Optional<Role> byNameAndRealmName = roleRepository.findByNameAndRealmName(role.getName(), role.getRealm().getName());
        Set<Resource> roleResources = byNameAndRealmName.get().getRoleResources();
        for(Resource resource : roleResources){
            if(resource.getName().equals(resourceName)) {
                resource.getPrivileges().remove(privilege);
                resourceRepository.save(resource); // Need?
            }
            else
            {
                throw new CustomException("Resource could not be found!",HttpStatus.NOT_FOUND);
            }
        }
    }
    @Transactional
    public Privilege createPrivilegeIfNotFound(String name) {

        Optional<Privilege> privilege = privilegeRepository.findByName(name);
        if(privilege.isPresent()){
            throw new CustomException("The privilege with the name [" + name + " ] already exists!",HttpStatus.CONFLICT);
        }
        else {
            Privilege newPrivilege = new Privilege(name,null);
            privilegeRepository.save(newPrivilege);
            return newPrivilege;
        }
    }

    public List<PrivilegeDto> getPrivilegesForResource(String resourceName,RoleDto role){
        Resource resource = resourceRepository.findByNameAndRoleNameAndRealmName(resourceName, role.getName(), role.getRealm().getName()).orElseThrow(() -> new CustomException("The resource could not be found!", HttpStatus.NOT_FOUND));
        return privilegeMapper.toPrivilegeDtoList(transferPrivilegesFromSetToList(resource.getPrivileges()));
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

    public Resource getResourceFromRoles(AppUserDto appUserDto,String resourceName){
        Optional<Resource> foundResource = Optional.empty();
        for (Role role: appUserDto.getRoles()){
            foundResource = resourceRepository.findByNameAndRolesContains(resourceName, role);
        }
        if(foundResource.isPresent())
            return foundResource.get();
        else
            throw new CustomException("The user does not have access to this resource!",HttpStatus.NOT_FOUND);
    }
    public void checkIfUserHasPrivilegeForResource(AppUserDto appUserDto, String resourceName,String requestType){
        String privilegeName = "";
        Set<Privilege> userPrivileges = getResourceFromRoles(appUserDto,resourceName).getPrivileges();
        String privilegeType = getPrivilegeTypeByRequestType(requestType);
        Privilege privilege = privilegeRepository.findByName(privilegeType).orElseThrow(() -> new CustomException("Privilege with the name [" + privilegeType + " ] could not be found for the user!", HttpStatus.NOT_FOUND));
        for (Privilege userPrivilege : userPrivileges) {
            if (userPrivilege.getName().equals(privilege.getName())) {
                privilegeName = privilege.getName();
                break;
            }
        }
            if (privilegeName.equals(""))
                throw new CustomException("The user does not have the privilege to do this!",HttpStatus.UNAUTHORIZED);
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

    public List<Privilege> transferPrivilegesFromSetToList(Set<Privilege> set){
        return new ArrayList<>(set);
    }
}
