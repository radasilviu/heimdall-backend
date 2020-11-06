package com.antonio.authserver.configuration;

import com.antonio.authserver.entity.*;
import com.antonio.authserver.repository.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.*;

@Component
@Log4j2
@Transactional
public class InitTestData implements ApplicationListener<ApplicationContextEvent> {

    private AppUserRepository appUserRepository;
    private RoleRepository roleRepository;
    private ClientRepository clientRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private RealmRepository realmRepository;
    private IdentityProviderRepository identityProviderRepository;
    private GroupRepository groupRepository;


    @Autowired
    public InitTestData(AppUserRepository appUserRepository, RoleRepository roleRepository, ClientRepository clientRepository, BCryptPasswordEncoder passwordEncoder, RealmRepository realmRepository, IdentityProviderRepository identityProviderRepository, GroupRepository groupRepository) {
        this.appUserRepository = appUserRepository;
        this.roleRepository = roleRepository;
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
        this.realmRepository = realmRepository;
        this.identityProviderRepository = identityProviderRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    public void onApplicationEvent(ApplicationContextEvent applicationContextEvent) {
        // for demo purpose
        if (roleRepository.findAll().size() == 0) {
            final List<Realm> realms = createRealms();
            final Map<String, IdentityProvider> identityProviders = createIdentityProviders();
            createRoles(realms);
            createUsers(realms, identityProviders);
            createClients(realms);
            groupRepository.save(new UserGroup("Group1", new ArrayList<>(), realms.get(1)));
        }
    }

    private Map<String, IdentityProvider> createIdentityProviders() {
        Map<String, IdentityProvider> identityProviders = new HashMap<>();
        final IdentityProvider google = new IdentityProvider("GOOGLE");
        final IdentityProvider usernameAndPassword = new IdentityProvider("USERNAME_AND_PASSWORD");
        identityProviders.put("google", google);
        identityProviders.put("username_password", usernameAndPassword);
        identityProviderRepository.saveAll(identityProviders.values());
        return identityProviders;
    }

    private List<Realm> createRealms() {
        List<Realm> realms = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Realm realm = new Realm();
            realm.setName("master" + i);
            realm.setDisplayName("Master " + i);
            realm.setEnabled(true);
            realms.add(realm);
        }
        realms = realmRepository.saveAll(realms);
        return realms;
    }

    private void createRoles(List<Realm> realms) {
        List<Role> roleList = Arrays.asList(new Role("ROLE_ADMIN", realms.get(0)), new Role("ROLE_USER", realms.get(0)));
        roleRepository.saveAll(roleList);
    }

    private void createClients(List<Realm> realms) {
        Client client = new Client("myClient", "clientPass", realms.get(0));
        clientRepository.save(client);
    }

    private void createUsers(List<Realm> realms, Map<String, IdentityProvider> identityProviders) {
        IdentityProvider usernameAndPassword = identityProviders.get("username_password");
        AppUser user = new AppUser("test", passwordEncoder.encode("test"), roleRepository.findAllByName("ROLE_USER"), "admin@gmail.com", true, null, realms.get(0), usernameAndPassword);
        AppUser admin = new AppUser("admin", passwordEncoder.encode("admin"), roleRepository.findAllByName("ROLE_ADMIN"), "test@gmail.com", true, null, realms.get(0), usernameAndPassword);
        appUserRepository.save(user);
        appUserRepository.save(admin);
    }
}
