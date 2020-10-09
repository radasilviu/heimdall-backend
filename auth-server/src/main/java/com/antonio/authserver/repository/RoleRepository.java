package com.antonio.authserver.repository;

import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    Set<Role> findAllByName(String name);
    @Transactional
    void deleteByName(String name);
    List<Role> findAllByRealmName(String name);
    Optional<Role> findByNameAndRealmName(String name,String realmName);
}
