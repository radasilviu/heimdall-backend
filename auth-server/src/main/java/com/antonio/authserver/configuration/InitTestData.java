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

import java.util.*;
import java.util.stream.Collectors;

@Component
@Log4j2
public class InitTestData implements ApplicationListener<ApplicationContextEvent> {



    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void onApplicationEvent(ApplicationContextEvent applicationContextEvent) {
        Role role = new Role("USER");
        roleRepository.save(role);
        List<Role> roleList = new ArrayList<Role>(Arrays.asList(new Role("ADMIN"),new Role("EDITOR"), new Role("VIEWER")));
        roleList.stream().forEach(r -> roleRepository.save(r));

        Set<Role> roles;
        roles = new HashSet<>(roleRepository.findAll());
        System.out.println(roles);


        AppUser appUser = new AppUser("admin","admin123",roles);
        appUserRepository.save(appUser);
    }


}
