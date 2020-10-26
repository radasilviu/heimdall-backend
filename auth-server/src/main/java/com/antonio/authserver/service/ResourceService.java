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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
@Service
@Transactional
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final PrivilegeRepository privilegeRepository;
    private final RoleRepository roleRepository;
    private final RoleService roleService;
    private final RoleMapper roleMapper;
    @Autowired
    public ResourceService(ResourceRepository resourceRepository, ResourceMapper resourceMapper, PrivilegeRepository privilegeRepository, RoleRepository roleRepository, RoleService roleService, RoleMapper roleMapper) {
        this.resourceRepository = resourceRepository;
        this.resourceMapper = resourceMapper;
        this.privilegeRepository = privilegeRepository;
        this.roleRepository = roleRepository;
        this.roleService = roleService;
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
    public void addResourceToRole(String realmName, String roleName, String resourceName) {
        Role role = roleService.findRoleByNameDaoAndRealmName(roleName, realmName);
        Resource resource = resourceRepository.findByNameAndRoleNameAndRealmName(resourceName, roleName, realmName).orElseThrow(() -> new CustomException("Resource with the name [" + resourceName + "] could not be found!", HttpStatus.NOT_FOUND));
        Set<Resource> roleResources = role.getRoleResources();
        roleResources.add(resource);
        roleRepository.save(role);
    }
    public void removeResourceFromRole(String realmName, String roleName, String resourceName) {
        Role role = roleService.findRoleByNameDaoAndRealmName(roleName, realmName);
        Resource resource = resourceRepository.findByNameAndRoleNameAndRealmName(resourceName, roleName, realmName).orElseThrow(() -> new CustomException("Resource with the name [" + resourceName + "] could not be found!", HttpStatus.NOT_FOUND));
        Set<Resource> roleResources = role.getRoleResources();
        roleResources.remove(resource);
        roleRepository.save(role);
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
    public void createResourceForRole(ResourceDto resourceDto, RoleDto roleDto) {
        Optional<Resource> byName = resourceRepository.findByNameAndRoleNameAndRealmName(resourceDto.getName(), roleDto.getName(), roleDto.getRealm().getName());
        if (!byName.isPresent()) {
            Resource resource = resourceMapper.toResourceDao(resourceDto);
            resource.setRoleName(roleDto.getName());
            resourceRepository.save(resource);
        }
    }
    public void createResourceForAllRoles(ResourceDto resourceDto) {
        List<Role> all = roleRepository.findAll();
        for (Role role : all) {
            if (!role.getName().equals("ROLE_ADMIN")) {
                Optional<Resource> byName = resourceRepository.findByNameAndRoleNameAndRealmName(resourceDto.getName(), role.getName(), role.getRealm().getName());
                if (!byName.isPresent()) {
                    Resource resource = resourceMapper.toResourceDao(resourceDto);
                    resource.setRoleName(role.getName());
                    resourceRepository.save(resource);
                }
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
}
