package com.antonio.authserver.repository;

import com.antonio.authserver.entity.Realm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RealmRepository extends JpaRepository<Realm, Long> {
}
