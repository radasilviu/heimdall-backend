package com.antonio.authserver.service;

import com.antonio.authserver.dto.IdentityProviderDto;
import com.antonio.authserver.entity.IdentityProvider;
import com.antonio.authserver.mapper.IdentityProviderMapper;
import com.antonio.authserver.model.CustomException;
import com.antonio.authserver.repository.IdentityProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IdentityProviderService {


    @Autowired
    private IdentityProviderRepository identityProviderRepository;


    public void saveIdentityProvider(IdentityProviderDto identityProviderDto) {
        Optional<IdentityProvider> optionalIdentityProvider = identityProviderRepository.findByProvider(identityProviderDto.getProvider());
        if (optionalIdentityProvider.isPresent()) {
            throw new CustomException("Identity Provider [ " + identityProviderDto.getProvider() + " ] already exists!",
                    HttpStatus.CONFLICT);
        }
        identityProviderRepository.save(IdentityProviderMapper.INSTANCE.toIdentityProviderDao(identityProviderDto));
    }

    public void updateIdentityProvider(IdentityProviderDto identityProviderDto) {
        Optional<IdentityProvider> optionalIdentityProvider = identityProviderRepository.findByProvider(identityProviderDto.getProvider());
        if (!optionalIdentityProvider.isPresent()) {
            throw new CustomException("Identity Provider [ " + identityProviderDto.getProvider() + " ] not found!",
                    HttpStatus.NOT_FOUND);
        }
        if (identityProviderDto.getProvider().equals(""))
            throw new CustomException("The inserted provider cannot be null!", HttpStatus.BAD_REQUEST);

        identityProviderRepository.save(IdentityProviderMapper.INSTANCE.toIdentityProviderDao(identityProviderDto));
    }

    public IdentityProviderDto findByProvider(String provider) {
        final IdentityProvider identityProvider = identityProviderRepository.findByProvider(provider).orElseThrow(() ->
                new CustomException("Identity Provider [ " + provider + " ] not found!", HttpStatus.NOT_FOUND));

        return IdentityProviderMapper.INSTANCE.toIdentityProviderDto(identityProvider);
    }
}
