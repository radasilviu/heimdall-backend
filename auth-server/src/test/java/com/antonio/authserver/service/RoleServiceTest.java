///*
//package com.antonio.authserver.service;
//import com.antonio.authserver.dto.RoleDto;
//import com.antonio.authserver.entity.AppUser;
//import com.antonio.authserver.entity.Role;
//import com.antonio.authserver.mapper.RoleMapper;
//import com.antonio.authserver.model.CustomException;
//import com.antonio.authserver.repository.AppUserRepository;
//import com.antonio.authserver.repository.RoleRepository;
//import org.checkerframework.checker.units.qual.A;
//import org.junit.Assert;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//@ExtendWith(MockitoExtension.class)
//class RoleServiceTest {
//
//    @Mock
//    private RoleRepository roleRepository;
//    @Mock
//    private AppUserRepository appUserRepository;
//    @Mock
//    private RoleMapper roleMapper;
//    @InjectMocks
//    private RoleService roleService;
//
//    @Test
//    void withGivenRoleDto_shouldSaveRole() {
//        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());
//        when(roleMapper.toRoleDao(any(RoleDto.class))).thenReturn(new Role("DummyRole"));
//        roleService.saveRole(new RoleDto("DummRole"));
//        verify(roleRepository,times(1)).save(any(Role.class));
//    }
//    @Test
//    void withGivenRoleDto_shouldReturnRoleAlreadyExistsException(){
//        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(new Role("Dummy")));
//        CustomException exception = assertThrows(CustomException.class,() -> roleService.saveRole(new RoleDto("Dummy")));
//        assertTrue(exception.getMessage().contains(" ] already exists!"));
//    }
//    @Test
//    void withGivenRoleDto_shouldReturnNullRoleException(){
//        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());
//        CustomException exception = assertThrows(CustomException.class,() -> roleService.saveRole(new RoleDto("")));
//        assertTrue(exception.getMessage().contains("The inserted Role cannot be null!"));
//    }
//    @Test
//    void shouldGetAllRoles() {
//        List<Role> roles = new ArrayList<>();
//        roles.add(new Role(""));
//        List<RoleDto> roleDtos = new ArrayList<>();
//        roleDtos.add(new RoleDto(""));
//        when(roleRepository.findAll()).thenReturn(roles);
//        when(roleMapper.toRoleDtoList(any())).thenReturn(roleDtos);
//        List<RoleDto> fromServ = roleService.getAllRoles();
//        assertEquals(fromServ.size(),roles.size());
//    }
//    @Test
//    void getRoleByName() {
//        Role role = new Role("DummyRole");
//        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(role));
//        when(roleMapper.toRoleDto(any())).thenReturn(new RoleDto("DummyRole"));
//        RoleDto roleDto = roleService.getRoleByName("DummyRole");
//        Assert.assertEquals(roleDto.getName(),role.getName());
//    }
//    @Test
//    void withGivenRoleName_shouldReturnRoleNotFoundException(){
//        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());
//        CustomException exception = assertThrows(CustomException.class,() -> roleService.getRoleByName("Dummy"));
//        assertTrue(exception.getMessage().contains(" could not be found!"));
//    }
//    @Test
//    void withGiveName_shouldUpdateRole() {
//        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(new Role("Dummy")));
//        roleService.updateRoleByName("Dummy",new RoleDto("NewDummy"));
//        verify(roleRepository,times(1)).save(any(Role.class));
//    }
//    @Test
//    void withGivenRoleNameAndRoleDto_shouldReturnRoleNotFoundExceptionForUpdate(){
//        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());
//        CustomException exception = assertThrows(CustomException.class,() -> roleService.updateRoleByName("Dummy",new RoleDto("NewDummy")));
//        assertTrue(exception.getMessage().contains("Role with the name ["));
//    }
//    @Test
//    void withGivenRoleNameAndRoleDto_shouldReturnNullRoleExceptionForUpdate(){
//        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(new Role("Dummy")));
//        CustomException exception = assertThrows(CustomException.class,() -> roleService.updateRoleByName("Dummy",new RoleDto("")));
//        assertTrue(exception.getMessage().contains("The inserted Role cannot be null!"));
//    }
//
//    @Test
//    void withGivenName_shouldDeleteRole() {
//        List<AppUser> list = new ArrayList<>();
//        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(new Role("Dummy")));
//        when(appUserRepository.findAllByRolesIn(anySet())).thenReturn(list);
//        roleService.deleteRoleByName("Dummy");
//        verify(roleRepository,times(1)).deleteByName("Dummy");
//    }
//    @Test
//    void withGivenName_shouldReturnDeleteAssignedRoleException(){
//        List<AppUser> list = new ArrayList<>();
//        list.add(new AppUser());
//        when(roleRepository.findByName(any())).thenReturn(Optional.of(new Role("Dummy")));
//        when(appUserRepository.findAllByRolesIn(any())).thenReturn(list);
//        CustomException exception = assertThrows(CustomException.class,() -> roleService.deleteRoleByName("Dummy"));
//        assertTrue(exception.getMessage().contains("Please make sure that all users don't have the role ["));
//    }
//    @Test
//    void withGivenName_shouldFindRole() {
//        Role found = new Role("Dummy");
//        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(found));
//        Role role = roleService.findRoleByNameDAO("Dummy");
//        Assert.assertEquals(role.getName(),found.getName());
//    }
//    @Test
//    void withGivenName_shouldReturnRoleNotFoundException(){
//        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());
//        CustomException exception = assertThrows(CustomException.class,() -> roleService.findRoleByNameDAO("Dummy"));
//        assertTrue(exception.getMessage().contains("could not be found!"));
//    }
//
//
//}*/
