package com.antonio.authserver.service;

import com.antonio.authserver.entity.IdentityProvider;
import com.antonio.authserver.model.CustomException;
import com.antonio.authserver.repository.IdentityProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IdentityProviderService {


    private IdentityProviderRepository identityProviderRepository;

    @Autowired
    public IdentityProviderService(IdentityProviderRepository identityProviderRepository) {
        this.identityProviderRepository = identityProviderRepository;
    }

    public void saveIdentityProvider(IdentityProvider identityProvider) {
        Optional<IdentityProvider> optionalIdentityProvider = identityProviderRepository.findByProvider(identityProvider.getProvider());
        if (optionalIdentityProvider.isPresent()) {
            throw new CustomException("Identity Provider [ " + identityProvider.getProvider() + " ] already exists!",
                    HttpStatus.CONFLICT);
        }
        identityProviderRepository.save(identityProvider);
    }

    public void updateIdentityProvider(IdentityProvider identityProvider) {
        Optional<IdentityProvider> optionalIdentityProvider = identityProviderRepository.findByProvider(identityProvider.getProvider());
        if (!optionalIdentityProvider.isPresent()) {
            throw new CustomException("Identity Provider [ " + identityProvider.getProvider() + " ] not found!",
                    HttpStatus.NOT_FOUND);
        }
        if (identityProvider.getProvider().equals(""))
            throw new CustomException("The inserted provider cannot be null!", HttpStatus.BAD_REQUEST);

        identityProviderRepository.save(identityProvider);
    }

    public IdentityProvider findByProvider(String provider) {
        final IdentityProvider identityProvider = identityProviderRepository.findByProvider(provider).orElseThrow(() ->
                new CustomException("Identity Provider [ " + provider + " ] not found!", HttpStatus.NOT_FOUND));

        return identityProvider;
    }
}
