package com.antonio.authserver.repository;

import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String name);

    Optional<AppUser> findByUsernameAndPassword(String username, String password);

    Optional<AppUser> findByCode(String code);

    List<AppUser> findAllByRolesIn(Set<Role> roles);

    void deleteByUsername(String username);

    Optional<AppUser> findByToken(String token);

    Optional<AppUser> findByRefreshToken(String refreshToken);
}
