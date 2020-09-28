package com.antonio.authserver.configuration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.transaction.Transactional;

import com.antonio.authserver.entity.Realm;
import com.antonio.authserver.repository.RealmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Client;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.repository.AppUserRepository;
import com.antonio.authserver.repository.ClientRepository;
import com.antonio.authserver.repository.RoleRepository;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
@Transactional
public class InitTestData implements ApplicationListener<ApplicationContextEvent> {

    private AppUserRepository appUserRepository;
    private RoleRepository roleRepository;
    private ClientRepository clientRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private RealmRepository realmRepository;

    @Autowired
    public InitTestData(AppUserRepository appUserRepository, RoleRepository roleRepository, ClientRepository clientRepository, BCryptPasswordEncoder passwordEncoder, RealmRepository realmRepository) {
        this.appUserRepository = appUserRepository;
        this.roleRepository = roleRepository;
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
        this.realmRepository = realmRepository;
    }

    @Override
    public void onApplicationEvent(ApplicationContextEvent applicationContextEvent) {
        // for demo purpose
        if (roleRepository.findAll().size() == 0) {
            List<Role> roleList = new ArrayList<Role>(Arrays.asList(new Role("ROLE_ADMIN"), new Role("ROLE_USER")));
            roleRepository.saveAll(roleList);

            AppUser user = new AppUser("test", passwordEncoder.encode("test"), roleRepository.findAllByName("ROLE_USER"), "smtp.mailtrap.io", true, null);
            AppUser admin = new AppUser("admin", passwordEncoder.encode("admin"), roleRepository.findAllByName("ROLE_ADMIN"), "smtp.mailtrap.io", true, null);
            AppUser admin_one = new AppUser("gabi", passwordEncoder.encode("gabi"), roleRepository.findAllByName("ROLE_ADMIN"), "smtp.mailtrap.io", true, null);
            AppUser admin_two = new AppUser("toni", passwordEncoder.encode("toni"), roleRepository.findAllByName("ROLE_ADMIN"), "smtp.mailtrap.io", true, null);
            appUserRepository.save(user);
            appUserRepository.save(admin);
            appUserRepository.save(admin_one);
            appUserRepository.save(admin_two);

            Client client = new Client("myClient", passwordEncoder.encode("clientPass"));
            clientRepository.save(client);
        }

        List<Realm> realms = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Realm realm = new Realm();
            realm.setName("master" + i);
            realm.setDisplayName("Master " + i);
            realm.setEnabled(true);
            realms.add(realm);
        }
        realmRepository.saveAll(realms);
    }
}
