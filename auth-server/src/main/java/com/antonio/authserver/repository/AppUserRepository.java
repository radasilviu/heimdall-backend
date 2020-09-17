package com.antonio.authserver.repository;

import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String name);

    Optional<AppUser> findByUsernameAndPassword(String username, String password);

    Optional<AppUser> findByCode(String code);
}
