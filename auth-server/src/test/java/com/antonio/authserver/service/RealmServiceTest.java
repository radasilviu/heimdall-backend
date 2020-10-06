package com.antonio.authserver.service;
import com.antonio.authserver.dto.RealmDto;
import com.antonio.authserver.dto.RoleDto;
import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Realm;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.mapper.RealmMapper;
import com.antonio.authserver.model.CustomException;
import com.antonio.authserver.repository.RealmRepository;
import com.antonio.authserver.request.RealmGeneralSettingRequest;
import com.antonio.authserver.request.RealmLoginSettingRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class RealmServiceTest {

    @Mock
    private RealmRepository realmRepository;
    @Mock
    private RealmMapper realmMapper;
    @InjectMocks
    private RealmService realmService;

    @Test
    void withGivenNameAndRealmGeneralSettingsRequest_shouldUpdateGeneralSettings() {
        when(realmRepository.findByName(anyString())).thenReturn(Optional.of(new Realm("Dummy","Dummy",false,false,false,false,false,false,false)));
        when(realmMapper.toRealmDto(any(Realm.class))).thenReturn(new RealmDto("Dummy","Dummy",false,false,false,false,false,false,false));
        realmService.updateGeneralSettings("Dummy",new RealmGeneralSettingRequest("Dummy","Dummy",true));
        verify(realmRepository,times(1)).save(any(Realm.class));
    }
    @Test
    void withGivenNameAndRealmGeneralSettingsRequest_shouldReturnRealmNotFoundException(){
        when(realmRepository.findByName(anyString())).thenReturn(Optional.empty());
        CustomException exception = assertThrows(CustomException.class,() -> realmService.updateGeneralSettings("Dummy",new RealmGeneralSettingRequest("NewDummy","NewDummy",true)));
        assertTrue(exception.getMessage().contains(" could not be found!"));
    }
    @Test
    void withGivenRealmLoginSettingRequest_shouldUpdateLoginSettings() {
        when(realmMapper.toRealmDto(any(Realm.class))).thenReturn(new RealmDto("Dummy","Dummy",false,false,false,false,false,false,false));
        when(realmRepository.findByName(anyString())).thenReturn(Optional.of(new Realm("Dummy","Dummy",false,false,false,false,false,false,false)));
        realmService.updateLoginSettings("Dummy",new RealmLoginSettingRequest(true,true,true,true,true,true));
        verify(realmRepository,times(1)).save(any(Realm.class));
    }
    @Test
    void withGivenNameAndRealmLoginSettingRequest_shouldReturnRealmNotFoundException(){
        when(realmRepository.findByName(anyString())).thenReturn(Optional.empty());
        CustomException exception = assertThrows(CustomException.class,() -> realmService.updateLoginSettings("Dummy",new RealmLoginSettingRequest(true,true,true,true,true,true)));
        assertTrue(exception.getMessage().contains(" could not be found!"));
    }
    @Test
    void shouldGetAllRealms() {
        List<Realm> realms = new ArrayList<>();
        realms.add(new Realm());
        List<RealmDto> realmDtos = new ArrayList<>();
        realmDtos.add(new RealmDto());
        when(realmRepository.findAll()).thenReturn(realms);
        when(realmMapper.toRealmDtoList(any())).thenReturn(realmDtos);
        List<RealmDto> fromServ = realmService.getAllRealms();
        assertEquals(fromServ.size(),realms.size());
    }
    @Test
    void shouldGetRealmByName() {
        Realm realm = new Realm("Dummy","Dummy",false,false,false,false,false,false,false);
        when(realmRepository.findByName(anyString())).thenReturn(Optional.of(realm));
        when(realmMapper.toRealmDto(any())).thenReturn(new RealmDto("Dummy","Dummy",false,false,false,false,false,false,false));
        RealmDto realmDto = realmService.getRealmByName("DummyRole");
        Assert.assertEquals(realmDto.getName(),realm.getName());
    }
    @Test
    void withGivenRealmName_shouldReturnRealmNotFoundException(){
        when(realmRepository.findByName(anyString())).thenReturn(Optional.empty());
        CustomException exception = assertThrows(CustomException.class,() -> realmService.getRealmByName("Dummy"));
        assertTrue(exception.getMessage().contains(" could not be found!"));
    }
    @Test
    void withGiveName_shouldUpdateRealm() {
        when(realmRepository.findByName(anyString())).thenReturn(Optional.of(new Realm("Dummy","Dummy",false,false,false,false,false,false,false)));
        realmService.updateRealmByName("Dummy",new RealmDto("NewDummy","Dummy",false,false,false,false,false,false,false));
        verify(realmRepository,times(1)).save(any(Realm.class));
    }
    @Test
    void withGivenRealmNameAndRealmDto_shouldReturnRealmNotFoundExceptionForUpdate(){
        when(realmRepository.findByName(anyString())).thenReturn(Optional.empty());
        CustomException exception = assertThrows(CustomException.class,() -> realmService.updateRealmByName("Dummy",new RealmDto("NewDummy","Dummy",false,false,false,false,false,false,false)));
        assertTrue(exception.getMessage().contains("Realm with the name ["));
    }
    @Test
    void withGivenRealmNameAndRealmDto_shouldReturnNullRealmExceptionForUpdate(){
        when(realmRepository.findByName(anyString())).thenReturn(Optional.of(new Realm("Dummy","Dummy",false,false,false,false,false,false,false)));
        CustomException exception = assertThrows(CustomException.class,() -> realmService.updateRealmByName("Dummy",new RealmDto("","",false,false,false,false,false,false,false)));
        assertTrue(exception.getMessage().contains("The inserted Realm cannot be null!"));
    }
    @Test
    void withGivenName_shouldDeleteRealm() {
        when(realmRepository.findByName(anyString())).thenReturn(Optional.of(new Realm("Dummy","Dummy",false,false,false,false,false,false,false)));
        realmService.deleteRealmByName("Dummy");
        verify(realmRepository,times(1)).delete(any(Realm.class));
    }

    @Test
    void withGivenRealmDto_shouldSaveRealm() {
        when(realmRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(realmMapper.toRealmDao(any(RealmDto.class))).thenReturn(new Realm("Dummy","Dummy",false,false,false,false,false,false,false));
        realmService.createRealm(new RealmDto("Dummy","Dummy",false,false,false,false,false,false,false));
        verify(realmRepository,times(1)).save(any(Realm.class));
    }
    @Test
    void withGivenRealmDto_shouldReturnRealmAlreadyExistsException(){
        when(realmRepository.findByName(anyString())).thenReturn(Optional.of(new Realm("Dummy","Dummy",false,false,false,false,false,false,false)));
        CustomException exception = assertThrows(CustomException.class,() -> realmService.createRealm(new RealmDto("Dummy","Dummy",false,false,false,false,false,false,false)));
        assertTrue(exception.getMessage().contains(" ] already exists!"));
    }
    @Test
    void withGivenRealmDto_shouldReturnNullRealmException(){
        when(realmRepository.findByName(anyString())).thenReturn(Optional.empty());
        CustomException exception = assertThrows(CustomException.class,() -> realmService.createRealm(new RealmDto("","",false,false,false,false,false,false,false)));
        assertTrue(exception.getMessage().contains("The inserted Realm cannot be null!"));
    }

}
