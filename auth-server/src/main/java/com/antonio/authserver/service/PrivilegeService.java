package com.antonio.authserver.service;
import com.antonio.authserver.configuration.constants.PrivilegeType;
import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.dto.PrivilegeDto;
import com.antonio.authserver.dto.RoleDto;
import com.antonio.authserver.entity.Privilege;
import com.antonio.authserver.entity.Resource;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.mapper.PrivilegeMapper;
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
public class PrivilegeService {

    private final PrivilegeRepository privilegeRepository;
    private final PrivilegeMapper privilegeMapper;
    private final RoleRepository roleRepository;
    private final RoleService roleService;
    private final ResourceService resourceService;
    private final ResourceRepository resourceRepository;
    @Autowired
    public PrivilegeService(PrivilegeRepository privilegeRepository, PrivilegeMapper privilegeMapper, RoleRepository roleRepository, RoleService roleService, ResourceService resourceService, ResourceRepository resourceRepository) {
        this.privilegeRepository = privilegeRepository;
        this.privilegeMapper = privilegeMapper;
        this.roleRepository = roleRepository;
        this.roleService = roleService;
        this.resourceService = resourceService;
        this.resourceRepository = resourceRepository;
    }

    public List<PrivilegeDto> getAllPrivileges(){
        return privilegeMapper.toPrivilegeDtoList(privilegeRepository.findAll());
    }
    public PrivilegeDto getPrivilegeByName(String name){
        Privilege privilege = privilegeRepository.findByName(name).orElseThrow(() -> new CustomException("The privilege with the name [" + name + " ] could not be found!", HttpStatus.NOT_FOUND));
        return privilegeMapper.toPrivilegeDto(privilege);
    }
    public void addPrivilegeToResource(String privilegeName, String resourceName, Role role){
        Privilege privilege = privilegeRepository.findByName(privilegeName).orElseThrow(() -> new CustomException("The privilege with the name [" + privilegeName + " ] could not be found!", HttpStatus.NOT_FOUND));
        Resource resource = resourceService.getResourceByNameAndRole(resourceName, role);
        Set<Privilege> privileges = resource.getPrivileges();
        privileges.add(privilege);
        resourceRepository.save(resource);
    }
    public void removePrivilegeFromRole(String privilegeName,String resourceName, Role role){
        Privilege privilege = privilegeRepository.findByName(privilegeName).orElseThrow(() -> new CustomException("The privilege with the name [" + privilegeName + " ] could not be found!", HttpStatus.NOT_FOUND));
        Resource resource = resourceService.getResourceByNameAndRole(resourceName, role);
        Set<Privilege> privileges = resource.getPrivileges();
        privileges.remove(privilege);
        resourceRepository.save(resource);
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

    public Set<Privilege> getPrivilegesForResource(String resourceName,Role role){
        Resource resource = resourceService.getResourceByNameAndRole(resourceName, role);
        return resource.getPrivileges();
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

}
