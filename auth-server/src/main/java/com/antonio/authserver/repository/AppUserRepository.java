package com.antonio.authserver.repository;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

	Optional<AppUser> findByToken(String token);

	@Query("SELECT u FROM AppUser u WHERE  u.emailCode =?1")
	Optional<AppUser> findByEmailCode(String emailCode);

	@Query("UPDATE AppUser u SET u.isActivated=true WHERE u.username= ?1")
	void activate(String username);
}
