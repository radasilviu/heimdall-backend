package com.antonio.authserver.service;

import com.antonio.authserver.entity.Realm;
import com.antonio.authserver.repository.RealmRepository;
import com.antonio.authserver.request.RealmGeneralSettingRequest;
import com.antonio.authserver.request.RealmLoginSettingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RealmService {

    @Autowired
    private RealmRepository realmRepository;

    public Realm updateGeneralSettings(RealmGeneralSettingRequest realm) {
        Optional<Realm> temp = realmRepository.findById(realm.getId());
        temp.get().setName(realm.getName());
        temp.get().setDisplayName(realm.getDisplayName());
        temp.get().setEnabled(realm.isEnabled());

        return realmRepository.save(temp.get());
    }

    public Realm updateLoginSettings(RealmLoginSettingRequest realm) {
        Optional<Realm> temp = realmRepository.findById(realm.getId());
        temp.get().setUserRegistration(realm.isUserRegistration());
        temp.get().setEditUsername(realm.isEditUsername());
        temp.get().setForgotPassword(realm.isForgotUsername());
        temp.get().setRememberMe(realm.isRememberMe());
        temp.get().setVerifyEmail(realm.isVerifyEmail());
        temp.get().setLoginWithEmail(realm.isLoginWithEmail());

        return realmRepository.save(temp.get());
    }
}
