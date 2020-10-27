package com.antonio.authserver.service;
import com.antonio.authserver.dto.ResourceDto;
import com.antonio.authserver.dto.RoleDto;
import com.antonio.authserver.entity.Privilege;
import com.antonio.authserver.entity.Resource;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.mapper.ResourceMapper;
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
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final PrivilegeRepository privilegeRepository;
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    @Autowired
    public ResourceService(ResourceRepository resourceRepository, ResourceMapper resourceMapper, PrivilegeRepository privilegeRepository, RoleRepository roleRepository, RoleMapper roleMapper) {
        this.resourceRepository = resourceRepository;
        this.resourceMapper = resourceMapper;
        this.privilegeRepository = privilegeRepository;
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }
    public List<ResourceDto> getAllResources() {
        return resourceMapper.toResourceDtoList(resourceRepository.findAll());
    }
    public List<ResourceDto> getAllResourcesForRole(RoleDto role) {
        return resourceMapper.toResourceDtoList(resourceRepository.findAllByRolesContains(roleMapper.toRoleDao(role)));
    }
    public Resource getResourceByNameAndRole(String resourceName, Role role) {
        return findResourceByNameAndRole(resourceName, role);
    }

    private Resource findResourceByNameAndRole(String resourceName, Role role) {
        return resourceRepository.findByNameAndRolesContains(resourceName, role).orElseThrow(() -> new CustomException("The resource with the name [" + resourceName + " ] could not be found!", HttpStatus.NOT_FOUND));
    }
    public void generateCompaniesAndBooksResourcesForUserRole() {
        Resource companies = new Resource("COMPANIES", null, "ROLE_USER", "master0", new HashSet<>());
        Resource books = new Resource("BOOKS", null, "ROLE_USER", "master0", new HashSet<>());
        resourceRepository.save(companies);
        resourceRepository.save(books);
    }
    public Set<Resource> getBasicResources() {
        Set<Resource> resources = new HashSet<>();
        resources.add(resourceRepository.findByNameAndRoleNameAndRealmName("COMPANIES", "ROLE_USER", "master0").get());
        resources.add(resourceRepository.findByNameAndRoleNameAndRealmName("BOOKS", "ROLE_USER", "master0").get());
        return resources;
    }
    public void assignAllPrivilegesToAllResources() {
        List<Resource> resources = resourceRepository.findAll();
        List<Privilege> privileges = privilegeRepository.findAll();
        for (Resource resource : resources) {
            for (Privilege privilege : privileges) {
                resource.getPrivileges().add(privilege);
            }
            resourceRepository.save(resource);
        }
    }
    public void createResourceForRole(ResourceDto resourceDto, Role role) {
        Optional<Resource> byName = resourceRepository.findByNameAndRoleNameAndRealmName(resourceDto.getName(), role.getName(), role.getRealm().getName());
        if (!byName.isPresent()) {
            Resource resource = resourceMapper.toResourceDao(resourceDto);
            resource.setRoleName(role.getName());
            resource.setRealmName(role.getRealm().getName());
            resourceRepository.save(resource);
        }
    }
    public void createResourceForAllRoles(ResourceDto resourceDto) {
        List<Role> all = roleRepository.findAll();
        for (Role role : all) {
            if (!role.getName().equals("ROLE_ADMIN")) {
               createResourceForRole(resourceDto,role);
            }
        }
    }
    public void deleteResourceForRole(String resourceName, RoleDto roleDto) {
        Resource resource = resourceRepository.findByNameAndRoleNameAndRealmName(resourceName, roleDto.getName(), roleDto.getRealm().getName()).orElseThrow(() -> new CustomException("The resource with the name [" + resourceName + "] could not be found!", HttpStatus.NOT_FOUND));
        resourceRepository.delete(resource);
    }
    public void deleteResourceForAllRoles(String resourceName) {
        List<Role> all = roleRepository.findAll();
        for (Role role : all) {
            if (!role.getName().equals("ROLE_ADMIN")) {
                Optional<Resource> byName = resourceRepository.findByNameAndRoleNameAndRealmName(resourceName, role.getName(), role.getRealm().getName());
                byName.ifPresent(resourceRepository::delete);
            }
        }
    }
    public Set<ResourceDto> getResourceDtosFromDatabaseForNewRole(){
        List<Resource> all = resourceRepository.findAll();
        Set<String> uniqueResourceNames = new HashSet<>();
        for (Resource resource : all) {
            uniqueResourceNames.add(resource.getName());
        }
        Set<ResourceDto> finalResources = new HashSet<>();
        for (String resourceName : uniqueResourceNames){
            finalResources.add(new ResourceDto(resourceName,new HashSet<>()));
        }
        return finalResources;
    }

    public void generateResourcesForNewRole(Role role){
        for (ResourceDto resourceDto : getResourceDtosFromDatabaseForNewRole()){
            createResourceForRole(resourceDto,role);
        }
    }

}
