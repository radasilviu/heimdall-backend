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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

            List<Realm> realms = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                Realm realm = new Realm();
                realm.setName("master" + i);
                realm.setDisplayName("Master " + i);
                realm.setEnabled(true);
                realms.add(realm);
            }
            realms = realmRepository.saveAll(realms);

            final IdentityProvider google = new IdentityProvider("GOOGLE");
            final IdentityProvider usernameAndPassword = new IdentityProvider("USERNAME_AND_PASSWORD");
            List<IdentityProvider> identityProviders = Arrays.asList(google, usernameAndPassword);
            identityProviderRepository.saveAll(identityProviders);

            List<Role> roleList = Arrays.asList(new Role("ROLE_ADMIN",realms.get(0)), new Role("ROLE_USER",realms.get(0)));
            roleRepository.saveAll(roleList);

            AppUser user = new AppUser("test", passwordEncoder.encode("test"), roleRepository.findAllByName("ROLE_USER"), "smtp.mailtrap.io", true, null, realms.get(0), usernameAndPassword);
            AppUser admin = new AppUser("admin", passwordEncoder.encode("admin"), roleRepository.findAllByName("ROLE_ADMIN"), "smtp.mailtrap.io", true, null, realms.get(0), usernameAndPassword);
            AppUser admin_one = new AppUser("gabi", passwordEncoder.encode("gabi"), roleRepository.findAllByName("ROLE_ADMIN"), "smtp.mailtrap.io", true, null, realms.get(0), usernameAndPassword);
            AppUser admin_two = new AppUser("toni", passwordEncoder.encode("toni"), roleRepository.findAllByName("ROLE_ADMIN"), "smtp.mailtrap.io", true, null, realms.get(0), usernameAndPassword);
            appUserRepository.save(user);
            appUserRepository.save(admin);
            appUserRepository.save(admin_one);
            appUserRepository.save(admin_two);

            Client client = new Client("myClient", "clientPass", realms.get(0));
            clientRepository.save(client);

            groupRepository.save(new UserGroup("Group1", new ArrayList<>(), realms.get(1)));
        }
    }
}
