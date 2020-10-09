package com.antonio.authserver.repository;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Role;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

	Optional<AppUser> findByUsername(String name);

	Optional<AppUser> findByUsernameAndPassword(String username, String password);

	Optional<AppUser> findByCode(String code);

	List<AppUser> findAllByRolesIn(Set<Role> roles);

	void deleteByUsername(String username);

	Optional<AppUser> findByEmailCode(String emailCode);

    Optional<AppUser> findByToken(String token);

    Optional<AppUser> findByRefreshToken(String refreshToken);

    Optional<AppUser> findByEmail(String email);

    Optional<AppUser> findByEmailAndForgotPasswordCode(String email, String forgotPasswordCode);

    Optional<AppUser> findByUsernameAndRealmName(String username, String realmName);

    List<AppUser> findAllByRealmName(String realmName);
}
