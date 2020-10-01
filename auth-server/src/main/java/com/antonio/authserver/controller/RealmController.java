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
@RequestMapping("api")
@CrossOrigin
public class RealmController {

    @Autowired
    private RealmRepository realmRepository;

    @Autowired
    private RealmService realmService;

    @GetMapping("/admin/realm/list")
    public List<Realm> list() {
        return realmRepository.findAll();
    }

    @PutMapping("/admin/realm/general-update")
    public Realm generalSettingUpdate(@RequestBody final RealmGeneralSettingRequest realm) {
        return realmService.updateGeneralSettings(realm);
    }

    @PutMapping("/admin/realm/login-update")
    public Realm loginSettingUpdate(@RequestBody final RealmLoginSettingRequest realm) {
        return realmService.updateLoginSettings(realm);
    }

    @GetMapping("/realm/check/{realm}")
    public Realm checkRealmExists(@PathVariable final String realm) {
        return realmService.getRealmByName(realm);
    }
}
