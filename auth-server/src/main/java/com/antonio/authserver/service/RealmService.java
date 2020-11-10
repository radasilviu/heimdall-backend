package com.antonio.authserver.service;

import com.antonio.authserver.dto.*;
import com.antonio.authserver.entity.Realm;
import com.antonio.authserver.mapper.*;
import com.antonio.authserver.model.CustomException;
import com.antonio.authserver.repository.*;
import com.antonio.authserver.request.RealmGeneralSettingRequest;
import com.antonio.authserver.request.RealmLoginSettingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RealmService {

    private RealmRepository realmRepository;
    private RealmMapper realmMapper;
    private RoleRepository roleRepository;
    private AppUserRepository appUserRepository;
    private ClientRepository clientRepository;
    private GroupRepository groupRepository;
    private ClientMapper clientMapper;
    private AppUserMapper appUserMapper;
    private RoleMapper roleMapper;
    private GroupMapper groupMapper;

    @Autowired
    public RealmService(RealmRepository realmRepository, RealmMapper realmMapper, RoleRepository roleRepository, AppUserRepository appUserRepository, ClientRepository clientRepository, GroupRepository groupRepository, ClientMapper clientMapper, AppUserMapper appUserMapper, RoleMapper roleMapper, GroupMapper groupMapper) {
        this.realmRepository = realmRepository;
        this.realmMapper = realmMapper;
        this.roleRepository = roleRepository;
        this.appUserRepository = appUserRepository;
        this.clientRepository = clientRepository;
        this.groupRepository = groupRepository;
        this.clientMapper = clientMapper;
        this.appUserMapper = appUserMapper;
        this.roleMapper = roleMapper;
        this.groupMapper = groupMapper;
    }

    public RealmDto updateGeneralSettings(String name, RealmGeneralSettingRequest realm) {
        Realm temp = realmRepository.findByName(name).orElseThrow(() -> new CustomException("Realm with the name [" + name + "] could not be found!", HttpStatus.NOT_FOUND));
        temp.setName(realm.getName());
        temp.setDisplayName(realm.getDisplayName());
        temp.setEnabled(realm.isEnabled());

        realmRepository.save(temp);
        return realmMapper.toRealmDto(temp);
    }

    public RealmDto updateLoginSettings(String name, RealmLoginSettingRequest realm) {
        Realm temp = realmRepository.findByName(name).orElseThrow(() -> new CustomException("Realm with the name [" + name + "] could not be found!", HttpStatus.NOT_FOUND));
        temp.setUserRegistration(realm.isUserRegistration());
        temp.setEditUsername(realm.isEditUsername());
        temp.setForgotPassword(realm.isForgotPassword());
        temp.setRememberMe(realm.isRememberMe());
        temp.setVerifyEmail(realm.isVerifyEmail());
        temp.setLoginWithEmail(realm.isLoginWithEmail());

        realmRepository.save(temp);
        return realmMapper.toRealmDto(temp);
    }

    public RealmDto getRealmByName(String realmName) {

        Realm realm = realmRepository.findByName(realmName).orElseThrow(() -> new CustomException("Realm with the name [" + realmName +" ] could not be found!",HttpStatus.NOT_FOUND));
        RealmDto realmDto = realmMapper.toRealmDto(realm);
        return realmDto;
    }

    public List<RealmDto> getAllRealms() {
        return realmMapper.toRealmDtoList(realmRepository.findAll());
    }

    public void createRealm(RealmDto realmDto) {
        realmDto.setName(realmDto.getName().replaceAll("\\s+", ""));
        Optional<Realm> realm = realmRepository.findByName(realmDto.getName());
        if (realm.isPresent())
            throw new CustomException("Realm with the name [ " + realm.get().getDisplayName() + " ] already exists!",
                    HttpStatus.CONFLICT);
        else if (realmDto.getName().equals("")) {
            throw new CustomException("The inserted Realm cannot be null!", HttpStatus.BAD_REQUEST);
        } else {
            realmRepository.save(realmMapper.toRealmDao(realmDto));
        }
    }

    public void updateRealmByName(String name, RealmDto realmDto) {
        realmDto.setName(realmDto.getName().replaceAll("\\s+", ""));
        Realm realm = realmRepository.findByName(name)
                .orElseThrow(() -> new CustomException("Realm with the name [ " + name + " ] could not be found!",
                        HttpStatus.NOT_FOUND));
        if (realmDto.getName().equals(""))
            throw new CustomException("The inserted Realm cannot be null!", HttpStatus.BAD_REQUEST);
        realm.setName(realmDto.getName());
        realm.setDisplayName(realmDto.getDisplayName());
        realmRepository.save(realm);
    }

    public void deleteRealmByName(String name) {
        Realm realm = realmRepository.findByName(name).orElseThrow(() -> new CustomException(
                "Realm with the name [ " + name + " ] could not be found!", HttpStatus.NOT_FOUND));
        realmRepository.delete(realm);
    }
}
