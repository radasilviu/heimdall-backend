package com.antonio.authserver.service;

import com.antonio.authserver.configuration.constants.ErrorMessage;
import com.antonio.authserver.entity.IdentityProvider;
import com.antonio.authserver.model.CustomException;
import com.antonio.authserver.repository.IdentityProviderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class IdentityProviderServiceTest {


    private static final String PROVIDER_NAME = "PROVIDER_NAME";
    private static final String UPDATED_PROVIDER_NAME = "UPDATED_PROVIDER_NAME";
    private static final String EXISTENT_PROVIDER_NAME = "EXISTENT_PROVIDER_NAME";
    private static final String NON_EXISTENT_PROVIDER_NAME = "NON_EXISTENT_PROVIDER_NAME";

    @Autowired
    private IdentityProviderService identityProviderService;

    @Autowired
    private IdentityProviderRepository identityProviderRepository;

    private IdentityProvider existingIdentityProvider;
    private IdentityProvider nonExistingIdentityProvider;


    @BeforeEach
    public void init() {
        existingIdentityProvider = new IdentityProvider();
        nonExistingIdentityProvider = new IdentityProvider();
        existingIdentityProvider.setProvider(EXISTENT_PROVIDER_NAME);
        nonExistingIdentityProvider.setProvider(NON_EXISTENT_PROVIDER_NAME);
    }

    @Test
    public void createIdentityProviderWithExistentProviderNameTest() {
        identityProviderRepository.save(existingIdentityProvider);

        CustomException exception = assertThrows(
                CustomException.class,
                () -> identityProviderService.saveIdentityProvider(existingIdentityProvider),
                "Expected saveIdentityProvider() to throw, but it didn't"
        );

        assertEquals(exception.getMessage(), ErrorMessage.IDENTITY_PROVIDER_EXIST.getMessage());

    }

    @Test
    public void updateIdentityProviderWithExistentProviderNameTest() {
        identityProviderRepository.save(existingIdentityProvider);

        existingIdentityProvider.setProvider(UPDATED_PROVIDER_NAME);
        identityProviderService.updateIdentityProvider(existingIdentityProvider, EXISTENT_PROVIDER_NAME);

        final Optional<IdentityProvider> identityProvider = identityProviderRepository.findByProvider(UPDATED_PROVIDER_NAME);

        assertTrue(identityProvider.isPresent());
        assertEquals(identityProvider.get().getProvider(), UPDATED_PROVIDER_NAME);

    }

    @Test
    public void updateIdentityProviderWithEmptyProviderName() {

        existingIdentityProvider.setProvider("");

        CustomException exception = assertThrows(
                CustomException.class,
                () -> identityProviderService.updateIdentityProvider(existingIdentityProvider, EXISTENT_PROVIDER_NAME),
                "Expected saveIdentityProvider() to throw, but it didn't"
        );

        assertEquals(exception.getMessage(), ErrorMessage.IDENTITY_PROVIDER_NOT_NULL.getMessage());

    }

    @Test
    public void createIdentityProviderWithNonExistentProviderNameTest() {

        nonExistingIdentityProvider.setProvider(PROVIDER_NAME);
        identityProviderService.saveIdentityProvider(nonExistingIdentityProvider);

        final Optional<IdentityProvider> identityProvider = identityProviderRepository.findByProvider(PROVIDER_NAME);

        assertTrue(identityProvider.isPresent());
        assertEquals(identityProvider.get().getProvider(), PROVIDER_NAME);

    }

    @Test
    public void updateIdentityProviderWithNonExistentProviderNameTest() {

        nonExistingIdentityProvider.setProvider(UPDATED_PROVIDER_NAME);
        CustomException exception = assertThrows(
                CustomException.class,
                () -> identityProviderService.updateIdentityProvider(nonExistingIdentityProvider, NON_EXISTENT_PROVIDER_NAME),
                "Expected saveIdentityProvider() to throw, but it didn't"
        );

        assertEquals(exception.getMessage(), ErrorMessage.IDENTITY_PROVIDER_NOT_FOUND.getMessage());
    }

    @Test
    public void findByProviderWithExistentProviderNameTest() {
        IdentityProvider identityProvider = identityProviderService.findByProvider(EXISTENT_PROVIDER_NAME);

        assertTrue(identityProvider != null);
        assertEquals(identityProvider.getProvider(), EXISTENT_PROVIDER_NAME);
    }

    @Test
    public void findByProviderWithNonExistentProviderNameTest() {

        CustomException exception = assertThrows(
                CustomException.class,
                () -> identityProviderService.findByProvider(NON_EXISTENT_PROVIDER_NAME),
                "Expected saveIdentityProvider() to throw, but it didn't"
        );

        assertEquals(exception.getMessage(), ErrorMessage.IDENTITY_PROVIDER_NOT_FOUND.getMessage());
    }


}
