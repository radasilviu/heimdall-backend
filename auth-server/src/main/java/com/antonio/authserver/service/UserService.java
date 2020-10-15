package com.antonio.authserver.service;

import com.antonio.authserver.configuration.constants.ErrorMessage;
import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Realm;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.mapper.AppUserMapper;
import com.antonio.authserver.model.CustomException;
import com.antonio.authserver.repository.AppUserRepository;
import com.antonio.authserver.repository.IdentityProviderRepository;
import com.antonio.authserver.repository.RealmRepository;
import com.antonio.authserver.repository.RoleRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private AppUserRepository appUserRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private AppUserMapper appUserMapper;
    private IdentityProviderRepository identityProviderRepository;
    private RealmRepository realmRepository;


    @Autowired
    public UserService(AppUserRepository appUserRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder, AppUserMapper appUserMapper, IdentityProviderRepository identityProviderRepository, RealmRepository realmRepository) {
        this.appUserRepository = appUserRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.appUserMapper = appUserMapper;
        this.identityProviderRepository = identityProviderRepository;
        this.realmRepository = realmRepository;
    }


    public List<AppUserDto> getAllUsers(String realmName) {
        return appUserMapper.toAppUserDtoList(appUserRepository.findAllByRealmName(realmName));
    }

    public AppUserDto getUserByUsername(String username) throws CustomException {
        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> new CustomException(
                "User with the username [ " + username + " ] could not be found!", HttpStatus.NOT_FOUND));
        return appUserMapper.toAppUserDto(appUser);
    }

    public AppUserDto getUserByUsernameAndRealmName(String realmName, String username) throws CustomException {
        AppUser appUser = appUserRepository.findByUsernameAndRealmName(username, realmName).orElseThrow(() -> new CustomException(
                "User with the username [ " + username + " ] could not be found!", HttpStatus.NOT_FOUND));
        return appUserMapper.toAppUserDto(appUser);
    }

    public void create(String realmName, AppUserDto appUserDto) throws CustomException {
        appUserDto.setUsername(appUserDto.getUsername().replaceAll("\\s+", ""));
        if (appUserRepository.findByUsernameAndRealmName(appUserDto.getUsername(), realmName).isPresent())
            throw new CustomException("User with the username [ " + appUserDto.getUsername() + " ] already exists!",
                    HttpStatus.CONFLICT);
        else if (appUserDto.getUsername().equals("")) {
            throw new CustomException("The inserted User cannot be null!", HttpStatus.BAD_REQUEST);
        } else {
            String randomCode = RandomString.make(64);
            appUserDto.setPassword(passwordEncoder.encode(appUserDto.getPassword()));
            appUserDto.setEmailCode(randomCode);
            appUserDto.setIsActivated(false);
            appUserDto.setRealm(realmRepository.findByName(realmName).get());
            appUserDto.setIdentityProvider(identityProviderRepository.findByProvider("USERNAME_AND_PASSWORD").get());
            appUserRepository.save(appUserMapper.toAppUserDao(appUserDto));

        }
    }

    public List<AppUser> isLoggedIn(String realmName) {
        List<AppUser> users = appUserRepository.findAllByRealmName(realmName);
        for (AppUser appUser : users) {
            if (appUser.getToken() == null) {
                appUser.setLoggedIn(false);
            } else {
                appUser.setLoggedIn(true);
            }
            appUserRepository.save(appUser);
        }
        return users;
    }


    public void logOutAll(Realm realm) {
        List<AppUser> users = appUserRepository.findAllByRealmName(realm.getName());
        Role role = roleRepository.findByName("ROLE_ADMIN").get();
        users.stream()
                .filter(u -> !u.getRoles().contains(role))
                .forEach(u -> {
                    u.setToken(null);
                    u.setLoggedIn(false);
                    u.setRefreshToken(null);
                });

        appUserRepository.saveAll(users);
    }

    public AppUserDto update(AppUserDto appUserDto) {
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

        return appUserDto;
    }

    public void updateUserByUsername(String realmName, String username, AppUserDto appUserDto) {
        appUserDto.setUsername(appUserDto.getUsername().replaceAll("\\s+", ""));
        AppUser appUser = appUserRepository.findByUsernameAndRealmName(username, realmName).orElseThrow(() -> new CustomException(
                "User with the username [ " + username + " ] could not be found!", HttpStatus.NOT_FOUND));
        if (appUserDto.getUsername().equals(""))
            throw new CustomException("The inserted User cannot be null!", HttpStatus.BAD_REQUEST);
        appUser.setUsername(appUserDto.getUsername());
        appUser.setPassword(appUserDto.getPassword());
        appUserRepository.save(appUser);
    }

    public AppUser addRole(String realmName, String username, Role role) throws CustomException {
        AppUser appUser = appUserRepository.findByUsernameAndRealmName(username, realmName).orElseThrow(() -> new CustomException(
                "User with the username [ " + username + " ] could not be found!", HttpStatus.NOT_FOUND));
        Role roleOptional = roleRepository.findByNameAndAndRealmName(role.getName(), realmName)
                .orElseThrow(() -> new CustomException(
                        "Cannot add the role [ " + role.getName() + " ] to the user. It needs to be created first.",
                        HttpStatus.BAD_REQUEST));
        if (roleOptional.getName().equals("ROLE_ADMIN"))
            adminAlreadyExists(username);
        appUser.getRoles().add(roleOptional);
        appUserRepository.save(appUser);

        return appUser;
    }

    public void removeRole(String realmName, String username, Role role) throws CustomException {
        AppUser appUser = appUserRepository.findByUsernameAndRealmName(username, realmName).orElseThrow(() -> new CustomException(
                "User with the username [ " + username + " ] could not be found!", HttpStatus.NOT_FOUND));
        Role roleOptional = roleRepository.findByNameAndAndRealmName(role.getName(), realmName)
                .orElseThrow(() -> new CustomException(
                        "Cannot add the role [ " + role.getName() + " ] to the user. It needs to be created first.",
                        HttpStatus.BAD_REQUEST));
        appUser.getRoles().remove(roleOptional);
        appUserRepository.save(appUser);

    }

    public void deleteUser(String realmName, String username) throws CustomException {
        AppUser appUser = appUserRepository.findByUsernameAndRealmName(username, realmName).orElseThrow(() -> new CustomException(
                "User with the username [ " + username + " ] could not be found!", HttpStatus.NOT_FOUND));
        appUserRepository.delete(appUser);

    }

    public AppUserDto findByUsernameAndRealmName(String username, String realm) {
        Optional<AppUser> user = appUserRepository.findByUsernameAndRealmName(username, realm);

        if (!user.isPresent()) {
            throw new CustomException(ErrorMessage.INVALID_CREDENTIALS.getMessage(), HttpStatus.BAD_REQUEST);
        }
        AppUserDto userDto = appUserMapper.toAppUserDto(user.get());
        return userDto;
    }

    public AppUserDto findByUsernameAndPasswordAndRealm(String username, String password, String realmName) {
        Optional<AppUser> userOptional = appUserRepository.findByUsernameAndRealmName(username, realmName);

        if (!userOptional.isPresent()) {
            throw new CustomException(ErrorMessage.INVALID_CREDENTIALS.getMessage(), HttpStatus.NOT_FOUND);
        }
        AppUserDto userDto = appUserMapper.toAppUserDto(userOptional.get());

        if (!passwordEncoder.matches(password, userDto.getPassword())) {
            throw new CustomException(ErrorMessage.INVALID_CREDENTIALS.getMessage(), HttpStatus.NOT_FOUND);
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

    public AppUserDto createSocialUser(AppUserDto appUserDto) {
        appUserDto.setUsername(appUserDto.getUsername().replaceAll("\\s+", ""));
        if (appUserRepository.findByUsername(appUserDto.getUsername()).isPresent())
            throw new CustomException("User with the username [ " + appUserDto.getUsername() + " ] already exists!",
                    HttpStatus.CONFLICT);
        else if (appUserDto.getUsername().equals("")) {
            throw new CustomException("The inserted [ username ] cannot be null!", HttpStatus.BAD_REQUEST);
        } else {
            String randomCode = RandomString.make(64);
            appUserDto.setEmailCode(randomCode);
            appUserDto.setIsActivated(false);


            final AppUser appUser = appUserMapper.toAppUserDao(appUserDto);

            appUserRepository.save(appUser);

        }

        return appUserDto;
    }

    public boolean verifyIfUserExist(String email) {
        return appUserRepository.findByEmail(email).isPresent();
    }

    public AppUserDto findByEmail(String email) {
        final Optional<AppUser> userOptional = appUserRepository.findByEmail(email);

        if (!userOptional.isPresent()) {
            throw new CustomException("User with the email [ " + email + " ] could not be found!", HttpStatus.NOT_FOUND);
        }

        return appUserMapper.toAppUserDto(userOptional.get());
    }

    public void adminAlreadyExists(String username) {
        List<AppUser> byUsername = appUserRepository.findAllByUsername(username);
        byUsername.forEach(appUser -> {
            boolean isAdmin = false;
            for (Role role : appUser.getRoles()) {
                if (role.getName().equals("ROLE_ADMIN")) {
                    isAdmin = true;
                    break;
                }
            }
            if (isAdmin)
                throw new CustomException("Admin with the name " + username + " already exists!", HttpStatus.CONFLICT);
        });
    }
}
