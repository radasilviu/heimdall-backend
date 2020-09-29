package com.antonio.authserver.controller;

import com.antonio.authserver.entity.Realm;
import com.antonio.authserver.repository.RealmRepository;
import com.antonio.authserver.request.RealmGeneralSettingRequest;
import com.antonio.authserver.request.RealmLoginSettingRequest;
import com.antonio.authserver.service.RealmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin/realm")
@CrossOrigin
public class RealmController {

    @Autowired
    private RealmRepository realmRepository;

    @Autowired
    private RealmService realmService;

    @GetMapping("/list")
    public List<Realm> list() {
        return realmRepository.findAll();
    }

    @PutMapping("/general-update")
    public Realm generalSettingUpdate(@RequestBody final RealmGeneralSettingRequest realm) {
        return realmService.updateGeneralSettings(realm);
    }

    @PutMapping("/login-update")
    public Realm loginSettingUpdate(@RequestBody final RealmLoginSettingRequest realm) {
        return realmService.updateLoginSettings(realm);
    }
}
