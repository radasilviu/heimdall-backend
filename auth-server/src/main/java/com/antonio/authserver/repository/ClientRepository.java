package com.antonio.authserver.repository;

import com.antonio.authserver.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByClientName(String clientName);

    Client findByClientNameAndClientSecretAndRealmName(String clientName, String clientSecret, String realmName);
}
