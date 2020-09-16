package com.antonio.authserver.service;

import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.model.Code;
import com.antonio.authserver.repository.AppUserRepository;
import com.antonio.authserver.request.ClientLoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private AppUserRepository appUserRepository;

    public Code getCode(ClientLoginRequest request) {
        AppUser user = appUserRepository.findByUsernameAndPassword(request.getUsername(), request.getPassword());

        if (user != null) {
            Code code = generateCode();
            return code;
        }

        return null;
    }

    private Code generateCode() {
        Code code = new Code();
        // TO DO: Figure out a flow for the code entity
        String hash = "someRandomPlaceholderText";
        code.setCode(hash);;
        return code;
    }
}
