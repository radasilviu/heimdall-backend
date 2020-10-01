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
@RequestMapping("api/admin/realm")
@CrossOrigin
public class RealmController {

    private RealmService realmService;

    @Autowired
    public RealmController(RealmService realmService) {
        this.realmService = realmService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<RealmDto>> getAllRealms() {
        return ResponseEntity.ok().body(realmService.getAllRealms());
    }

    @GetMapping("/{name}")
    public ResponseEntity<RealmDto> getRealmByName(@PathVariable String name){
        return ResponseEntity.ok().body(realmService.getRealmByName(name));
    }
    @PostMapping
    public ResponseEntity<ResponseMessage> createRealm(@RequestBody RealmDto realmDto){
        realmService.createRealm(realmDto);
        final ResponseMessage responseMessage = new ResponseMessage("Realm successfully created");
        return ResponseEntity.ok().body(responseMessage);
    }
    @PutMapping("/{name}")
    public ResponseEntity<ResponseMessage> updateRealmByName(@PathVariable String name,@RequestBody RealmDto realmDto){
        realmService.updateRealmByName(name,realmDto);
        final ResponseMessage responseMessage = new ResponseMessage("Realm successfully updated");
        return ResponseEntity.ok().body(responseMessage);
    }
    @DeleteMapping("/{name}")
    public ResponseEntity<ResponseMessage> deleteRealmByName(@PathVariable String name){
        realmService.deleteRealmByName(name);
        final ResponseMessage responseMessage = new ResponseMessage("Realm successfully deleted");
        return ResponseEntity.ok().body(responseMessage);
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
