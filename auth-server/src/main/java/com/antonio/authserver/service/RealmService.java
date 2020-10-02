package com.antonio.authserver.service;

import com.antonio.authserver.configuration.constants.ErrorMessage;
import com.antonio.authserver.entity.Realm;
import com.antonio.authserver.model.CustomException;
import com.antonio.authserver.repository.RealmRepository;
import com.antonio.authserver.request.RealmGeneralSettingRequest;
import com.antonio.authserver.request.RealmLoginSettingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RealmService {

    @Autowired
    private RealmRepository realmRepository;

    public Realm updateGeneralSettings(RealmGeneralSettingRequest realm) {
        Realm temp = realmRepository.findById(realm.getId()).orElseThrow(
                () -> new CustomException(ErrorMessage.REALM_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND));
        temp.setName(realm.getName());
        temp.setDisplayName(realm.getDisplayName());
        temp.setEnabled(realm.isEnabled());

        return realmRepository.save(temp);
    }

    public Realm updateLoginSettings(RealmLoginSettingRequest realm) {
        Realm temp = realmRepository.findById(realm.getId()).orElseThrow(
                () -> new CustomException(ErrorMessage.REALM_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND));
        temp.setUserRegistration(realm.isUserRegistration());
        temp.setEditUsername(realm.isEditUsername());
        temp.setForgotPassword(realm.isForgotPassword());
        temp.setRememberMe(realm.isRememberMe());
        temp.setVerifyEmail(realm.isVerifyEmail());
        temp.setLoginWithEmail(realm.isLoginWithEmail());

        return realmRepository.save(temp);
    }

    public Realm getRealmByName(String realm) {
        Realm temp = realmRepository.findByName(realm).orElseThrow(
                () -> new CustomException(ErrorMessage.REALM_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND));
        return temp;
    }
}
