package com.antonio.authserver.service;

import com.antonio.authserver.dto.RealmDto;
import com.antonio.authserver.entity.Client;
import com.antonio.authserver.entity.Realm;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.mapper.RealmMapper;
import com.antonio.authserver.model.CustomException;
import com.antonio.authserver.repository.RealmRepository;
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

    @Autowired
    public RealmService(RealmRepository realmRepository, RealmMapper realmMapper) {
        this.realmRepository = realmRepository;
        this.realmMapper = realmMapper;
    }
    public RealmDto updateGeneralSettings(RealmGeneralSettingRequest realm) {
        Optional<Realm> temp = realmRepository.findByName(realm.getName());
        temp.get().setName(realm.getName());
        temp.get().setDisplayName(realm.getDisplayName());
        temp.get().setEnabled(realm.isEnabled());

        return realmMapper.toRealmDto(realmRepository.save(temp.get()));
    }

    public RealmDto updateLoginSettings(RealmLoginSettingRequest realm) {
        Optional<Realm> temp = realmRepository.findByName(realm.getName());
        temp.get().setUserRegistration(realm.isUserRegistration());
        temp.get().setEditUsername(realm.isEditUsername());
        temp.get().setForgotPassword(realm.isForgotUsername());
        temp.get().setRememberMe(realm.isRememberMe());
        temp.get().setVerifyEmail(realm.isVerifyEmail());
        temp.get().setLoginWithEmail(realm.isLoginWithEmail());

        return realmMapper.toRealmDto(realmRepository.save(temp.get()));
    }

    public List<RealmDto> getAllRealms(){
        return realmMapper.toRealmDtoList(realmRepository.findAll());
    }
    public RealmDto getRealmByName(String name){
        Realm realm = realmRepository.findByName(name)
                .orElseThrow(() -> new CustomException("Realm with the name [" + name + "] could not be found!",
                        HttpStatus.NOT_FOUND));
        return realmMapper.toRealmDto(realm);
    }
    public void createRealm(RealmDto realmDto){
        realmDto.setName(realmDto.getName().replaceAll("\\s+", ""));
        Optional<Realm> realm = realmRepository.findByName(realmDto.getName());
        if (realm.isPresent())
            throw new CustomException("Realm with the name [ " + realm.get().getDisplayName() + " ] already exists!",
                    HttpStatus.CONFLICT);
        else if (realmDto.getName().equals("")) {
            throw new CustomException("The inserted Role cannot be null!", HttpStatus.BAD_REQUEST);
        } else {
            realmRepository.save(realmMapper.toRealmDao(realmDto));
        }
    }
    public void updateRealmByName(String name,RealmDto realmDto){
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
    public void deleteRealmByName(String name){
        Realm realm = realmRepository.findByName(name).orElseThrow(() -> new CustomException(
                "Realm with the name [ " + name + " ] could not be found!", HttpStatus.NOT_FOUND));
        realmRepository.delete(realm);
    }
}
