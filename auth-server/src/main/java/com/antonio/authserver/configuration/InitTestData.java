package com.antonio.authserver.configuration;

import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Client;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.repository.AppUserRepository;
import com.antonio.authserver.repository.ClientRepository;
import com.antonio.authserver.repository.RoleRepository;
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

    @Autowired
    public InitTestData(AppUserRepository appUserRepository, RoleRepository roleRepository, ClientRepository clientRepository, BCryptPasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.roleRepository = roleRepository;
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ApplicationContextEvent applicationContextEvent) {
        List<Role> roleList = new ArrayList<Role>(Arrays.asList(new Role("ROLE_ADMIN"), new Role("ROLE_USER")));
        roleRepository.saveAll(roleList);

        AppUser user = new AppUser("test", passwordEncoder.encode("test"), roleRepository.findAllByName("ROLE_USER"));
        AppUser admin = new AppUser("admin", passwordEncoder.encode("admin"), roleRepository.findAllByName("ROLE_ADMIN"));
        AppUser admin_one = new AppUser("gabi", passwordEncoder.encode("gabi"), roleRepository.findAllByName("ROLE_ADMIN"));
        AppUser admin_two = new AppUser("toni", passwordEncoder.encode("toni"), roleRepository.findAllByName("ROLE_ADMIN"));
        appUserRepository.save(user);
        appUserRepository.save(admin);
        appUserRepository.save(admin_one);
        appUserRepository.save(admin_two);

        Client client = new Client("myclient1");
        clientRepository.save(client);

    }


}
