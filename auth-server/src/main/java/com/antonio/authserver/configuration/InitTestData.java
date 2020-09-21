package com.antonio.authserver.configuration;

import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.repository.AppUserRepository;
import com.antonio.authserver.repository.RoleRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Log4j2
public class InitTestData implements ApplicationListener<ApplicationContextEvent> {


    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private RoleRepository roleRepository;


    @Override
    public void onApplicationEvent(ApplicationContextEvent applicationContextEvent) {
        List<Role> roleList = new ArrayList<Role>(Arrays.asList(new Role("ROLE_ADMIN"), new Role("ROLE_USER")));
        roleRepository.saveAll(roleList);

        AppUser appUser = new AppUser("admin", "admin", roleRepository.findAllByName("ROLE_ADMIN"));
        AppUser appUser1 = new AppUser("gabi", "gabi", roleRepository.findAllByName("ROLE_ADMIN"));
        AppUser appUser2 = new AppUser("toni", "toni", roleRepository.findAllByName("ROLE_ADMIN"));
        appUserRepository.save(appUser);
        appUserRepository.save(appUser1);
        appUserRepository.save(appUser2);
    }


}
