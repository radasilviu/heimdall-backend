package com.antonio.authserver.configuration;

import com.antonio.authserver.entity.*;
import com.antonio.authserver.repository.*;
import com.antonio.authserver.service.PrivilegeService;
import com.antonio.authserver.service.ResourceService;
import com.antonio.authserver.service.RoleService;
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

    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;
    private final ClientRepository clientRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RealmRepository realmRepository;
    private final IdentityProviderRepository identityProviderRepository;
    private final GroupRepository groupRepository;
    private final PrivilegeService privilegeService;
    private final RoleService roleService;
    private final ResourceRepository resourceRepository;
    private final PrivilegeRepository privilegeRepository;
    private final RoleResourcePrivilegeRepository roleResourcePrivilegeRepository;

    @Autowired
    public InitTestData(AppUserRepository appUserRepository, RoleRepository roleRepository, ClientRepository clientRepository, BCryptPasswordEncoder passwordEncoder, RealmRepository realmRepository, IdentityProviderRepository identityProviderRepository, GroupRepository groupRepository, PrivilegeService privilegeService, RoleService roleService, ResourceRepository resourceRepository, PrivilegeRepository privilegeRepository, RoleResourcePrivilegeRepository roleResourcePrivilegeRepository) {
        this.appUserRepository = appUserRepository;
        this.roleRepository = roleRepository;
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
        this.realmRepository = realmRepository;
        this.identityProviderRepository = identityProviderRepository;
        this.groupRepository = groupRepository;
        this.privilegeService = privilegeService;
        this.roleService = roleService;
        this.resourceRepository = resourceRepository;
        this.privilegeRepository = privilegeRepository;
        this.roleResourcePrivilegeRepository = roleResourcePrivilegeRepository;
    }

    @Override
    public void onApplicationEvent(ApplicationContextEvent applicationContextEvent) {
        // for demo purpose
        if (roleRepository.findAll().size() == 0) {
            List<Realm> realms = initRealms();
            final IdentityProvider usernameAndPassword = initIdentityProvider();
            initPrivileges();
            initResources();
            initRoles(realms);
            initUsers(realms, usernameAndPassword);
            initClients(realms);
            initGroups(realms);
        }
    }

    private void initGroups(List<Realm> realms) {
        groupRepository.save(new UserGroup("Group1", new ArrayList<>(), realms.get(1)));
    }

    private void initPrivileges(){
        privilegeService.createPrivileges();
    }

    private void initResources(){
            Resource companies = new Resource("COMPANIES");
            Resource books = new Resource("BOOKS");
            resourceRepository.save(companies);
            resourceRepository.save(books);
}

    private void initClients(List<Realm> realms) {
        Client client = new Client("myClient", "clientPass", realms.get(0));
        clientRepository.save(client);
    }

    private void initUsers(List<Realm> realms, IdentityProvider usernameAndPassword) {
        AppUser user = new AppUser("test", passwordEncoder.encode("test"), roleRepository.findAllByName("ROLE_USER"), "smtp.mailtrap.io", true, null, realms.get(0), usernameAndPassword);
        AppUser admin = new AppUser("admin", passwordEncoder.encode("admin"), roleRepository.findAllByName("ROLE_ADMIN"), "smtp.mailtrap.io", true, null, realms.get(0), usernameAndPassword);
        appUserRepository.save(user);
        appUserRepository.save(admin);
    }

    private IdentityProvider initIdentityProvider() {
        final IdentityProvider google = new IdentityProvider("GOOGLE");
        final IdentityProvider usernameAndPassword = new IdentityProvider("USERNAME_AND_PASSWORD");
        List<IdentityProvider> identityProviders = Arrays.asList(google, usernameAndPassword);
        identityProviderRepository.saveAll(identityProviders);
        return usernameAndPassword;
    }

    private void initRoles(List<Realm> realms) {
        Role admin = new Role("ROLE_ADMIN", realms.get(0), null);
        Role user = new Role("ROLE_USER", realms.get(0), new HashSet<>());
        List<Role> roleList = Arrays.asList(admin, user);
        roleRepository.saveAll(roleList);
        roleService.addResourceToRole(user.getRealm().getName(), user.getName(), "COMPANIES");
        roleService.addResourceToRole(user.getRealm().getName(), user.getName(), "BOOKS");
        assignAllPrivilegesForRoleUser(roleRepository.findByNameAndRealmName("ROLE_USER","master0").get());
    }

    private List<Realm> initRealms() {
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
    //for basic role_user with all privileges
    private Set<Resource> getBasicResourcesForRoleUserDemo() {
        Set<Resource> resources = new HashSet<>();
        resources.add(resourceRepository.findByName("COMPANIES").get());
        resources.add(resourceRepository.findByName("BOOKS").get());
        return resources;
    }
    //for basic role_user with all privileges
    private void assignAllPrivilegesForRoleUser(Role role) {
        Set<Resource> basicResources = getBasicResourcesForRoleUserDemo();
        List<Privilege> privileges = privilegeRepository.findAll();
        for (Resource resource : basicResources){
            RoleResourcePrivilege roleResourcePrivilege = getRoleResourcePrivilegeByRoleAndResource(role, resource).get();
            roleResourcePrivilege.getPrivileges().addAll(privileges);
            roleResourcePrivilegeRepository.save(roleResourcePrivilege);
        }
    }
    private Optional<RoleResourcePrivilege> getRoleResourcePrivilegeByRoleAndResource(Role role,Resource resource){
        return roleResourcePrivilegeRepository.findByRoleAndResource(role, resource);
    }
}
