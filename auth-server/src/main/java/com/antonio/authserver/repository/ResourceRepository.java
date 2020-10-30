package com.antonio.authserver.repository;
import com.antonio.authserver.entity.Resource;
import com.antonio.authserver.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ResourceRepository extends JpaRepository<Resource,Long> {
    Optional<Resource> findByName(String name);
    Optional<Resource> findByNameAndRoleNameAndRealmName(String name,String roleName,String realmName);
    List<Resource> findAllByRoleNameAndRealmName(String roleName,String realmName);
}
