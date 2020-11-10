package com.antonio.authserver.repository;
import com.antonio.authserver.entity.Privilege;
import com.antonio.authserver.entity.Resource;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.entity.RoleResourcePrivilege;
import org.checkerframework.checker.nullness.Opt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
@Repository
public interface RoleResourcePrivilegeRepository extends JpaRepository<RoleResourcePrivilege,Long> {
    Optional<RoleResourcePrivilege> findByRoleAndResource(Role role, Resource resource);
}
