package com.antonio.authserver.service;

import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Code;
import com.antonio.authserver.repository.AppUserRepository;
import com.antonio.authserver.repository.CodeRepository;
import com.antonio.authserver.request.ClientLoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {
    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private CodeRepository codeRepository;

    public Code getCode(ClientLoginRequest request) {
        Optional<AppUser> userOptional = appUserRepository.findByUsernameAndPassword(request.getUsername(), request.getPassword());

        if (userOptional.isEmpty()) {
            throw new RuntimeException("Bad credentials!");
        }

        Code code = createOauthCode();
        final AppUser user = userOptional.get();
        saveUserWithNewCodeValue(user, code);

        return code;
    }

    private void saveUserWithNewCodeValue(AppUser user, Code code) {
        user.setCode(code.getCode());
        appUserRepository.save(user);

    }

    private Code createOauthCode() {
        final Code code = new Code(generateCode());

        return code;
    }

    private String generateCode() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 6;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }
}
