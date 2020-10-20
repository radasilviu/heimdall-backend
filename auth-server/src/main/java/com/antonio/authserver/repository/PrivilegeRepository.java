package com.antonio.authserver.repository;
import com.antonio.authserver.entity.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege,Long> {
    Optional<Privilege> findByNameAndResource(String name,String resourceName);
    Optional<Privilege> findByName(String name);
}
