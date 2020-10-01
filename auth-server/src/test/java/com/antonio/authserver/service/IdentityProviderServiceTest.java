package com.antonio.authserver.service;

import com.antonio.authserver.configuration.constants.ErrorMessage;
import com.antonio.authserver.entity.IdentityProvider;
import com.antonio.authserver.model.CustomException;
import com.antonio.authserver.repository.IdentityProviderRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IdentityProviderServiceTest {


    private static final String PROVIDER_NAME = "PROVIDER_NAME";
    private static final String UPDATED_PROVIDER_NAME = "UPDATED_PROVIDER_NAME";
    private static final String SYSTEM_PROVIDER_NAME = "EXISTENT_PROVIDER_NAME";

    @Autowired
    private IdentityProviderService identityProviderService;

    @Autowired
    private IdentityProviderRepository identityProviderRepository;

    private IdentityProvider identityProvider;

    @BeforeAll
    public void setup() {
        identityProvider = new IdentityProvider();
        identityProvider.setProvider(SYSTEM_PROVIDER_NAME);
        identityProviderRepository.save(identityProvider);
    }

    @Test
    public void createIdentityProviderWithExistentProviderNameTest() {
        identityProvider.setProvider(SYSTEM_PROVIDER_NAME);

        CustomException exception = assertThrows(
                CustomException.class,
                () -> identityProviderService.saveIdentityProvider(identityProvider),
                "Expected saveIdentityProvider() to throw, but it didn't"
        );

        assertEquals(exception.getMessage(), ErrorMessage.IDENTITY_PROVIDER_EXIST.getMessage());

    }

    @Test
    public void createIdentityProviderWithNonExistentProviderNameTest() {

        identityProvider.setProvider(PROVIDER_NAME);
        identityProviderService.saveIdentityProvider(identityProvider);

        final Optional<IdentityProvider> identityProvider = identityProviderRepository.findByProvider(PROVIDER_NAME);

        assertTrue(identityProvider.isPresent());
        assertEquals(identityProvider.get().getProvider(), PROVIDER_NAME);

    }

    @Test
    public void updateIdentityProviderWithNonExistentProviderNameTest() {

        identityProvider.setProvider(UPDATED_PROVIDER_NAME);

        CustomException exception = assertThrows(
                CustomException.class,
                () -> identityProviderService.updateIdentityProvider(identityProvider, PROVIDER_NAME),
                "Expected saveIdentityProvider() to throw, but it didn't"
        );

        assertEquals(exception.getMessage(), ErrorMessage.IDENTITY_PROVIDER_NOT_FOUND.getMessage());
    }


    @Test
    public void updateIdentityProviderWithExistentProviderNameTest() {
        identityProvider.setProvider(UPDATED_PROVIDER_NAME);
        identityProviderService.updateIdentityProvider(identityProvider, SYSTEM_PROVIDER_NAME);

        final Optional<IdentityProvider> identityProvider = identityProviderRepository.findByProvider(UPDATED_PROVIDER_NAME);

        assertTrue(identityProvider.isPresent());
        assertEquals(identityProvider.get().getProvider(), UPDATED_PROVIDER_NAME);

    }

}
