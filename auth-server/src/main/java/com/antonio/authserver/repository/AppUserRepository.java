package com.antonio.authserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.antonio.authserver.entity.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, String> {
}