package com.antonio.authserver.service;
import com.antonio.authserver.dto.ResourceDto;
import com.antonio.authserver.entity.Privilege;
import com.antonio.authserver.entity.Resource;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.entity.RoleResourcePrivilege;
import com.antonio.authserver.mapper.ResourceMapper;
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
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final PrivilegeRepository privilegeRepository;
    private final RoleRepository roleRepository;
    private final RoleResourcePrivilegeRepository roleResourcePrivilegeRepository;
    @Autowired
    public ResourceService(ResourceRepository resourceRepository, ResourceMapper resourceMapper, PrivilegeRepository privilegeRepository, RoleRepository roleRepository, RoleResourcePrivilegeRepository roleResourcePrivilegeRepository) {
        this.resourceRepository = resourceRepository;
        this.resourceMapper = resourceMapper;
        this.privilegeRepository = privilegeRepository;
        this.roleRepository = roleRepository;
        this.roleResourcePrivilegeRepository = roleResourcePrivilegeRepository;
    }

    public List<ResourceDto> getAllResourcesFromDb(){
        return resourceMapper.toResourceDtoList(resourceRepository.findAll());
    }
    public void updateResourceByName(String oldName,String newName){
        newName = newName.trim();
        if(newName.equals(""))
            throw new CustomException("The inserted name cannot be null!", HttpStatus.BAD_REQUEST);
        if(resourceRepository.findByName(newName).isPresent())
            throw new CustomException("There already is a resource with that name!",HttpStatus.CONFLICT);
        Resource resource = getResourceByNameOrThrowExceptionIfNotFound(oldName);
        resource.setName(newName);
        resourceRepository.save(resource);
    }
    //for basic role_user with all privileges
    public Set<Resource> getBasicResourcesForRoleUserDemo() {
        Set<Resource> resources = new HashSet<>();
        resources.add(resourceRepository.findByName("COMPANIES").get());
        resources.add(resourceRepository.findByName("BOOKS").get());
        return resources;
    }
    //for basic role_user with all privileges
    public void assignAllPrivilegesForRoleUser(Role role) {
        Set<Resource> basicResources = getBasicResourcesForRoleUserDemo();
        List<Privilege> privileges = privilegeRepository.findAll();
        for (Resource resource : basicResources){
            RoleResourcePrivilege roleResourcePrivilege = getRoleResourcePrivilegeByRoleAndResource(role, resource).get();
            roleResourcePrivilege.getPrivileges().addAll(privileges);
            roleResourcePrivilegeRepository.save(roleResourcePrivilege);
        }
    }

    public void addResourceToDb(ResourceDto resourceDto){
        String name = resourceDto.getName();
        name = name.trim();
        if(name.equals(""))
            throw new CustomException("The inserted name cannot be null!", HttpStatus.BAD_REQUEST);
        Optional<Resource> foundResource = resourceRepository.findByName(name);
        if(foundResource.isPresent())
            throw new CustomException("The resource with the name [" + name + "] already exists!",HttpStatus.CONFLICT);
        else{
            Resource resource = resourceMapper.toResourceDao(resourceDto);
            resourceRepository.save(resource);
        }
    }
    public void removeResourceFromDb(String resourceName){
        Resource resource = getResourceByNameOrThrowExceptionIfNotFound(resourceName);
        List<Role> roles = roleRepository.findAll();
        for (Role role : roles){
            role.getRoleResources().remove(resource);
        }
        resourceRepository.delete(resource);
    }
    public Set<ResourceDto> getAllResourcesForRole(String realmName,String roleName){
        Role role = getRoleByRealmNameAndNameOrThrowExceptionIfNotFound(realmName, roleName);
        return resourceMapper.toResourceDtoSet(role.getRoleResources());
    }

    private Resource getResourceByNameOrThrowExceptionIfNotFound(String resourceName){
        return resourceRepository.findByName(resourceName).orElseThrow(() -> new CustomException("The resource with the name [" + resourceName + "] could not be found!", HttpStatus.NOT_FOUND));
    }
    private Role getRoleByRealmNameAndNameOrThrowExceptionIfNotFound(String realmName,String roleName){
        return roleRepository.findByNameAndRealmName(roleName,realmName).orElseThrow(() -> new CustomException("Role with the name [" + roleName +"] could not be found!",HttpStatus.NOT_FOUND));
    }
    private Optional<RoleResourcePrivilege> getRoleResourcePrivilegeByRoleAndResource(Role role,Resource resource){
        return roleResourcePrivilegeRepository.findByRoleAndResource(role, resource);
    }
}
