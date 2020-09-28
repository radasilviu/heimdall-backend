package com.antonio.authserver.service;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.mapper.AppUserMapper;
import com.antonio.authserver.model.exceptions.controllerexceptions.*;
import com.antonio.authserver.repository.AppUserRepository;
import com.antonio.authserver.repository.RoleRepository;
import net.bytebuddy.utility.RandomString;

@Service
@Transactional
public class UserService {

	private AppUserRepository appUserRepository;
	private RoleRepository roleRepository;
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	public UserService(AppUserRepository appUserRepository, RoleRepository roleRepository,
			BCryptPasswordEncoder passwordEncoder) {
		this.appUserRepository = appUserRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public List<AppUserDto> getAllUsers() {
		return AppUserMapper.INSTANCE.toAppUserDtoList(appUserRepository.findAll());
	}


    public AppUserDto getUserByUsername(String username) throws UserNotFound {
        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> new UserNotFound(username));
        return AppUserMapper.INSTANCE.toAppUserDto(appUser);
    }

	public void create(AppUserDto appUserDto) throws UserAlreadyExists, NullResource {
		appUserDto.setUsername(appUserDto.getUsername().replaceAll("\\s+", ""));
		if (appUserRepository.findByUsername(appUserDto.getUsername()).isPresent())
			throw new UserAlreadyExists(appUserDto.getUsername());
		else if (appUserDto.getUsername().equals("")) {
			throw new NullResource("User");
		} else {
			String randomCode = RandomString.make(64);
			appUserDto.setPassword(passwordEncoder.encode(appUserDto.getPassword()));
			appUserDto.setEmailCode(randomCode);
			appUserDto.setIsActivated(false);
			appUserRepository.save(AppUserMapper.INSTANCE.toAppUserDao(appUserDto));

		}
	}

	public void update(AppUserDto appUserDto) {

		appUserDto.setUsername(appUserDto.getUsername().replaceAll("\\s+", ""));
		final AppUser appUser = appUserRepository.findByUsername(appUserDto.getUsername())
				.orElseThrow(() -> new UserNotFound(appUserDto.getUsername()));

		final AppUser userToUpdate = AppUserMapper.INSTANCE.toAppUserDao(appUserDto);

		if (appUserDto.getUsername().equals("")) {
			throw new NullResource("User");
		}

		userToUpdate.setId(appUser.getId());
		appUserRepository.save(userToUpdate);

	}

	public void updateUserByUsername(String username, AppUserDto appUserDto) {
		appUserDto.setUsername(appUserDto.getUsername().replaceAll("\\s+", ""));
		AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> new UserNotFound(username));
		if (appUserDto.getUsername().equals(""))
			throw new NullResource("User");
		appUser.setUsername(appUserDto.getUsername());
		appUser.setPassword(appUserDto.getPassword());
		appUserRepository.save(appUser);
	}

	public AppUser addRole(String username, Role role) throws UserNotFound, CannotAddRole {
		AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> new UserNotFound(username));
		roleRepository.findByName(role.getName()).orElseThrow(() -> new CannotAddRole(role.getName()));

		appUser.getRoles().add(role);
		appUserRepository.save(appUser);

		return appUser;
	}

	public void removeRole(String username, Role role) throws UserNotFound, CannotAddRole {
		AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> new UserNotFound(username));
		roleRepository.findByName(role.getName()).orElseThrow(() -> new CannotAddRole(role.getName()));

		appUser.getRoles().remove(role);
		appUserRepository.save(appUser);

	}

	public void deleteUser(String username) throws UserNotFound {
		AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> new UserNotFound(username));
		appUserRepository.delete(appUser);

	}

	public AppUserDto findByUsernameAndPassword(String username, String password) {
		Optional<AppUser> userOptional = appUserRepository.findByUsername(username);

		if (!userOptional.isPresent()) {
			throw new UserNotFound(username);
		}
		AppUserDto userDto = AppUserMapper.INSTANCE.toAppUserDto(userOptional.get());

		if (!passwordEncoder.matches(password, userDto.getPassword())) {
			throw new IncorrectPassword(password);
		}

		return userDto;
	}

	public void verifyUserCode(String code) {

		AppUser appUser = appUserRepository.findByCode(code).orElseThrow(() -> new CodeNotFound(code));
		appUser.setCode(null);

		appUserRepository.save(appUser);

	}

	public AppUserDto findByUsername(String username) {
		final Optional<AppUser> userOptional = appUserRepository.findByUsername(username);

		if (!userOptional.isPresent()) {
			throw new UserNotFound(username);
		}

		return AppUserMapper.INSTANCE.toAppUserDto(userOptional.get());
	}

	public AppUserDto findUserByToken(String token) {
		AppUser appUser = appUserRepository.findByToken(token).orElseThrow(() -> new TokenNotFound(token));
		return AppUserMapper.INSTANCE.toAppUserDto(appUser);
	}

	public AppUserDto findUserByRefreshToken(String refreshToken) {
		AppUser appUser = appUserRepository.findByRefreshToken(refreshToken)
				.orElseThrow(() -> new RefreshTokenNotFound(refreshToken));

		return AppUserMapper.INSTANCE.toAppUserDto(appUser);
	}
}
