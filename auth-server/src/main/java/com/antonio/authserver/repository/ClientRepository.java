package com.antonio.authserver.repository;

import com.antonio.authserver.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByClientName(String clientName);

    Optional<Client> findByClientNameAndClientSecretAndRealmName(String clientName, String clientSecret, String realmName);

    List<Client> findAllByRealmName(String realmName);

    Optional<Client> findByClientNameAndRealmName(String clientName,String realmName);
}
