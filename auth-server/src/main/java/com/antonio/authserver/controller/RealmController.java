package com.antonio.authserver.controller;

import com.antonio.authserver.dto.RealmDto;
import com.antonio.authserver.entity.Realm;
import com.antonio.authserver.model.ResponseMessage;
import com.antonio.authserver.repository.RealmRepository;
import com.antonio.authserver.request.RealmGeneralSettingRequest;
import com.antonio.authserver.request.RealmLoginSettingRequest;
import com.antonio.authserver.service.RealmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
@CrossOrigin
public class RealmController {

    private RealmService realmService;

    @Autowired
    public RealmController(RealmService realmService) {
        this.realmService = realmService;
    }

    @GetMapping("/admin/realm//list")
    public ResponseEntity<List<RealmDto>> getAllRealms() {
        return ResponseEntity.ok().body(realmService.getAllRealms());
    }

    @GetMapping("/admin/realm//{name}")
    public ResponseEntity<RealmDto> getRealmByName(@PathVariable String name){
        return ResponseEntity.ok().body(realmService.getRealmByName(name));
    }
    @PostMapping("/admin/realm/")
    public ResponseEntity<ResponseMessage> createRealm(@RequestBody RealmDto realmDto){
        realmService.createRealm(realmDto);
        final ResponseMessage responseMessage = new ResponseMessage("Realm successfully created");
        return ResponseEntity.ok().body(responseMessage);
    }
    @PutMapping("/admin/realm//{name}")
    public ResponseEntity<ResponseMessage> updateRealmByName(@PathVariable String name,@RequestBody RealmDto realmDto){
        realmService.updateRealmByName(name,realmDto);
        final ResponseMessage responseMessage = new ResponseMessage("Realm successfully updated");
        return ResponseEntity.ok().body(responseMessage);
    }
    @DeleteMapping("/admin/realm//{name}")
    public ResponseEntity<ResponseMessage> deleteRealmByName(@PathVariable String name){
        realmService.deleteRealmByName(name);
        final ResponseMessage responseMessage = new ResponseMessage("Realm successfully deleted");
        return ResponseEntity.ok().body(responseMessage);
    }

    @PutMapping("/admin/realm/general-update/{name}")
    public RealmDto generalSettingUpdate(@PathVariable String name,@RequestBody final RealmGeneralSettingRequest realm) {
        return realmService.updateGeneralSettings(name,realm);
    }

    @PutMapping("/admin/realm/login-update/{name}")
    public RealmDto loginSettingUpdate(@PathVariable String name,@RequestBody final RealmLoginSettingRequest realm) {
        return realmService.updateLoginSettings(name,realm);
    }

    @GetMapping("/realm/check/{realm}")
    public RealmDto checkRealmExists(@PathVariable final String realm) {
        return realmService.getRealmByName(realm);
    }
}
