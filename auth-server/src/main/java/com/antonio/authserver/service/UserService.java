package com.antonio.authserver.service;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.mapper.AppUserMapper;
import com.antonio.authserver.model.CustomException;;
import com.antonio.authserver.repository.AppUserRepository;
import com.antonio.authserver.repository.RoleRepository;
import net.bytebuddy.utility.RandomString;

@Service
@Transactional
public class UserService {

	private AppUserRepository appUserRepository;
	private RoleRepository roleRepository;
	private BCryptPasswordEncoder passwordEncoder;
	private AppUserMapper appUserMapper;

	@Autowired
	public UserService(AppUserRepository appUserRepository, RoleRepository roleRepository,
					   BCryptPasswordEncoder passwordEncoder, AppUserMapper appUserMapper) {
		this.appUserRepository = appUserRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.appUserMapper = appUserMapper;
	}

	public List<AppUserDto> getAllUsers() {
		return AppUserMapper.INSTANCE.toAppUserDtoList(appUserRepository.findAll());
	}

	public AppUserDto getUserByUsername(String username) throws CustomException {
		AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> new CustomException(
				"User with the username [ " + username + " ] could not be found!", HttpStatus.NOT_FOUND));
		return appUserMapper.toAppUserDto(appUser);
	}

	public void create(AppUserDto appUserDto) throws CustomException {
		appUserDto.setUsername(appUserDto.getUsername().replaceAll("\\s+", ""));
		if (appUserRepository.findByUsername(appUserDto.getUsername()).isPresent())
			throw new CustomException("User with the username [ " + appUserDto.getUsername() + " ] already exists!",
					HttpStatus.CONFLICT);
		else if (appUserDto.getUsername().equals("")) {
			throw new CustomException("The inserted User cannot be null!", HttpStatus.BAD_REQUEST);
		} else {
			String randomCode = RandomString.make(64);
			appUserDto.setPassword(passwordEncoder.encode(appUserDto.getPassword()));
			appUserDto.setEmailCode(randomCode);
			appUserDto.setIsActivated(false);
			appUserRepository.save(appUserMapper.toAppUserDao(appUserDto));

		}
	}

	public void update(AppUserDto appUserDto) {

		appUserDto.setUsername(appUserDto.getUsername().replaceAll("\\s+", ""));
		final AppUser appUser = appUserRepository.findByUsername(appUserDto.getUsername())
				.orElseThrow(() -> new CustomException(
						"User with the username [ " + appUserDto.getUsername() + " ] could not be found!",
						HttpStatus.NOT_FOUND));

		final AppUser userToUpdate = appUserMapper.toAppUserDao(appUserDto);

		if (appUserDto.getUsername().equals("")) {
			throw new CustomException("The inserted User cannot be null!", HttpStatus.BAD_REQUEST);
		}

		userToUpdate.setId(appUser.getId());
		appUserRepository.save(userToUpdate);

	}

	public void updateUserByUsername(String username, AppUserDto appUserDto) {
		appUserDto.setUsername(appUserDto.getUsername().replaceAll("\\s+", ""));
		AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> new CustomException(
				"User with the username [ " + username + " ] could not be found!", HttpStatus.NOT_FOUND));
		if (appUserDto.getUsername().equals(""))
			throw new CustomException("The inserted User cannot be null!",HttpStatus.BAD_REQUEST);
		appUser.setUsername(appUserDto.getUsername());
		appUser.setPassword(appUserDto.getPassword());
		appUserRepository.save(appUser);
	}

	public AppUser addRole(String username, Role role) throws CustomException {
		AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> new CustomException(
				"User with the username [ " + username + " ] could not be found!", HttpStatus.NOT_FOUND));
		roleRepository.findByName(role.getName())
				.orElseThrow(() -> new CustomException(
						"Cannot add the role [ " + role.getName() + " ] to the user. It needs to be created first.",
						HttpStatus.BAD_REQUEST));

		appUser.getRoles().add(role);
		appUserRepository.save(appUser);

		return appUser;
	}

	public void removeRole(String username, Role role) throws CustomException {
		AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> new CustomException(
				"User with the username [ " + username + " ] could not be found!", HttpStatus.NOT_FOUND));
		roleRepository.findByName(role.getName())
				.orElseThrow(() -> new CustomException(
						"Cannot add the role [ " + role.getName() + " ] to the user. It needs to be created first.",
						HttpStatus.BAD_REQUEST));

		appUser.getRoles().remove(role);
		appUserRepository.save(appUser);

	}

	public void deleteUser(String username) throws CustomException {
		AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> new CustomException(
				"User with the username [ " + username + " ] could not be found!", HttpStatus.NOT_FOUND));
		appUserRepository.delete(appUser);

	}

	public AppUserDto findByUsernameAndPassword(String username, String password) {
		Optional<AppUser> userOptional = appUserRepository.findByUsername(username);

		if (!userOptional.isPresent()) {
			throw new CustomException("User with the username [ " + username + " ] could not be found!",
					HttpStatus.NOT_FOUND);
		}
		AppUserDto userDto = appUserMapper.toAppUserDto(userOptional.get());

		if (!passwordEncoder.matches(password, userDto.getPassword())) {
			throw new CustomException("Invalid Credentials", HttpStatus.UNAUTHORIZED);
		}

		return userDto;
	}

	public void verifyUserCode(String code) {

		AppUser appUser = appUserRepository.findByCode(code).orElseThrow(
				() -> new CustomException("Code [ " + code + " ] could not be found!", HttpStatus.NOT_FOUND));
		appUser.setCode(null);

		appUserRepository.save(appUser);

	}

	public AppUserDto findByUsername(String username) {
		final Optional<AppUser> userOptional = appUserRepository.findByUsername(username);

		if (!userOptional.isPresent()) {
			throw new CustomException("User with the username [ " + username + " ] could not be found!",
					HttpStatus.NOT_FOUND);
		}

		return appUserMapper.toAppUserDto(userOptional.get());
	}

	public AppUserDto findUserByToken(String token) {
		AppUser appUser = appUserRepository.findByToken(token).orElseThrow(
				() -> new CustomException("Token [ " + token + " ] could not be found!", HttpStatus.NOT_FOUND));
		return appUserMapper.toAppUserDto(appUser);
	}

	public AppUserDto findUserByRefreshToken(String refreshToken) {
		AppUser appUser = appUserRepository.findByRefreshToken(refreshToken)
				.orElseThrow(() -> new CustomException("RefreshToken [ " + refreshToken + " ] could not be found!",
						HttpStatus.NOT_FOUND));

		return appUserMapper.toAppUserDto(appUser);
	}
}
