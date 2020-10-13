package com.antonio.authserver.repository;

import com.antonio.authserver.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<UserGroup, Long> {
    Optional<UserGroup> findByName(String name);
    void deleteByName(String name);
    Optional<UserGroup> findByNameAndRealmName(String name, String realmName);
    List<UserGroup> findAllByRealmName(String realmName);
}
