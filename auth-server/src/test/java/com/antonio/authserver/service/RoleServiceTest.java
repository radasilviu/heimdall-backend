package com.antonio.authserver.service;
import com.antonio.authserver.dto.RoleDto;
import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Realm;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.mapper.RoleMapper;
import com.antonio.authserver.model.CustomException;
import com.antonio.authserver.repository.AppUserRepository;
import com.antonio.authserver.repository.RealmRepository;
import com.antonio.authserver.repository.RoleRepository;
import org.checkerframework.checker.units.qual.A;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private RoleMapper roleMapper;
    @Mock
    private RealmRepository realmRepository;
    @InjectMocks
    private RoleService roleService;

    @Test
    void withGivenRealmNameAndRoleDto_shouldSaveRole() {
        Realm realm = new Realm("Realm","Realm",false,false,false,false,false,false,false);
        when(roleRepository.findByNameAndRealmName(anyString(),anyString())).thenReturn(Optional.empty());
        when(realmRepository.findByName(anyString())).thenReturn(Optional.of(realm));
        when(roleMapper.toRoleDao(any(RoleDto.class))).thenReturn(new Role("DummyRole",realm));
        roleService.saveRole("Realm",new RoleDto("DummRole",realm));
        verify(roleRepository,times(1)).save(any(Role.class));
    }
    @Test
    void withGivenRealmNameAndRoleDto_shouldReturnRoleAlreadyExistsException(){
        Realm realm = new Realm("Realm","Realm",false,false,false,false,false,false,false);
        when(roleRepository.findByNameAndRealmName(anyString(),anyString())).thenReturn(Optional.of(new Role("Dummy",realm)));
        CustomException exception = assertThrows(CustomException.class,() -> roleService.saveRole("Realm",new RoleDto("Dummy",realm)));
        assertTrue(exception.getMessage().contains(" ] already exists!"));
    }
    @Test
    void withRealmNameAndRoleDto_shouldReturnNullRoleException(){
        when(roleRepository.findByNameAndRealmName(anyString(),anyString())).thenReturn(Optional.empty());
        CustomException exception = assertThrows(CustomException.class,() -> roleService.saveRole("Realm",new RoleDto("",null)));
        assertTrue(exception.getMessage().contains("The inserted Role cannot be null!"));
    }
    @Test
    void withGivenRealmNameAndRoleDto_shouldReturnYouCantCreateMoreAdminRolesExceptionForSave(){
        Realm realm = new Realm("Realm","Realm",false,false,false,false,false,false,false);
        CustomException exception = assertThrows(CustomException.class,() -> roleService.saveRole("Realm",new RoleDto("ROLE_ADMIN",realm)));
        assertTrue(exception.getMessage().contains("You are not allowed to create additional ADMIN roles."));
    }

    @Test
    void withGivenRealmName_shouldGetAllRoles() {
        Realm realm = new Realm("Realm","Realm",false,false,false,false,false,false,false);
        List<Role> roles = new ArrayList<>();
        roles.add(new Role("",realm));
        roles.add(new Role("ROLE_ADMIN",realm));
        List<RoleDto> roleDtos = new ArrayList<>();
        roleDtos.add(new RoleDto("",realm));
        when(roleRepository.findAllByRealmName(anyString())).thenReturn(roles);
        when(roleMapper.toRoleDtoList(any())).thenReturn(roleDtos);
        List<RoleDto> fromServ = roleService.getAllRoles("Realm");
        assertEquals(fromServ.size(),roles.size());
    }
    @Test
    void withGivenRealmNameAndRoleName_shouldReturnRole() {
        Realm realm = new Realm("Realm","Realm",false,false,false,false,false,false,false);
        Role role = new Role("DummyRole",realm);
        when(roleRepository.findByNameAndRealmName(anyString(),anyString())).thenReturn(Optional.of(role));
        when(roleMapper.toRoleDto(any())).thenReturn(new RoleDto("DummyRole",realm));
        RoleDto roleDto = roleService.getRoleByName("Realm","DummyRole");
        Assert.assertEquals(roleDto.getName(),role.getName());
    }
    @Test
    void withGivenRealmNameAndRoleName_shouldReturnRoleNotFoundException(){
        when(roleRepository.findByNameAndRealmName(anyString(),anyString())).thenReturn(Optional.empty());
        CustomException exception = assertThrows(CustomException.class,() -> roleService.getRoleByName("Dummy","Realm"));
        assertTrue(exception.getMessage().contains(" could not be found!"));
    }
    @Test
    void withGivenRealmNameAndRoleNameAndRoleDto_shouldUpdateRole() {
        Realm realm = new Realm("Realm","Realm",false,false,false,false,false,false,false);
        when(roleRepository.findByNameAndRealmName(anyString(),anyString())).thenReturn(Optional.of(new Role("Dummy",realm)));
        roleService.updateRoleByName("Realm","Dummy",new RoleDto("NewDummy",realm));
        verify(roleRepository,times(1)).save(any(Role.class));
    }
    @Test
    void withGivenRealmNameAndRoleDto_shouldReturnYouCantCreateMoreAdminRolesException(){
        Realm realm = new Realm("Realm","Realm",false,false,false,false,false,false,false);
        CustomException exception = assertThrows(CustomException.class,() -> roleService.updateRoleByName("Realm","Dummy",new RoleDto("ROLE_ADMIN",realm)));
        assertTrue(exception.getMessage().contains("You are not allowed to create additional ADMIN roles."));
    }
    @Test
    void withGivenRealmNameAndRoleNameAndRoleDto_shouldReturnRoleNotFoundExceptionForUpdate(){
        Realm realm = new Realm("Realm","Realm",false,false,false,false,false,false,false);
        when(roleRepository.findByNameAndRealmName(anyString(),anyString())).thenReturn(Optional.empty());
        CustomException exception = assertThrows(CustomException.class,() -> roleService.updateRoleByName("Realm","Dummy",new RoleDto("NewDummy",realm)));
        assertTrue(exception.getMessage().contains("Role with the name ["));
    }
    @Test
    void withGivenRealmNameAndRoleNameAndRoleDto_shouldReturnNullRoleExceptionForUpdate(){
        Realm realm = new Realm("Realm","Realm",false,false,false,false,false,false,false);
        when(roleRepository.findByNameAndRealmName(anyString(),anyString())).thenReturn(Optional.of(new Role("Dummy",realm)));
        CustomException exception = assertThrows(CustomException.class,() -> roleService.updateRoleByName("Realm","Dummy",new RoleDto("",null)));
        assertTrue(exception.getMessage().contains("The inserted Role cannot be null!"));
    }

    @Test
    void withGivenNameAndRealmName_shouldDeleteRole() {
        Realm realm = new Realm("Realm","Realm",false,false,false,false,false,false,false);
        List<AppUser> list = new ArrayList<>();
        when(roleRepository.findByNameAndRealmName(anyString(),anyString())).thenReturn(Optional.of(new Role("Dummy",realm)));
        when(appUserRepository.findAllByRolesIn(anySet())).thenReturn(list);
        roleService.deleteRoleByName("Realm","Dummy");
        verify(roleRepository,times(1)).deleteByName("Dummy");
    }
    @Test
    void withGivenNameAndRealmName_shouldReturnDeleteAssignedRoleException(){
        Realm realm = new Realm("Realm","Realm",false,false,false,false,false,false,false);
        List<AppUser> list = new ArrayList<>();
        list.add(new AppUser());
        when(roleRepository.findByNameAndRealmName(any(),anyString())).thenReturn(Optional.of(new Role("Dummy",realm)));
        when(appUserRepository.findAllByRolesIn(any())).thenReturn(list);
        CustomException exception = assertThrows(CustomException.class,() -> roleService.deleteRoleByName("Realm","Dummy"));
        assertTrue(exception.getMessage().contains("Please make sure that all users don't have the role ["));
    }
    @Test
    void withGivenNameAndRealmName_shouldFindRole() {
        Realm realm = new Realm("Realm","Realm",false,false,false,false,false,false,false);
        Role found = new Role("Dummy",realm);
        when(roleRepository.findByNameAndRealmName(anyString(),anyString())).thenReturn(Optional.of(found));
        Role role = roleService.findRoleByNameDaoAndRealmName("Dummy","Realm");
        Assert.assertEquals(role.getName(),found.getName());
    }
    @Test
    void withGivenNameAndRealmName_shouldReturnRoleNotFoundException(){
        when(roleRepository.findByNameAndRealmName(anyString(),anyString())).thenReturn(Optional.empty());
        CustomException exception = assertThrows(CustomException.class,() -> roleService.findRoleByNameDaoAndRealmName("Dummy","Realm"));
        assertTrue(exception.getMessage().contains("could not be found!"));
    }


}
