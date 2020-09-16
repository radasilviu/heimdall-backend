//package com.antonio.authserver.configuration;
//
//import com.antonio.authserver.entity.AppUser;
//import com.antonio.authserver.entity.Post;
//import com.antonio.authserver.repository.AppUserRepository;
//import com.antonio.authserver.repository.PostRepository;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationListener;
//import org.springframework.context.event.ApplicationContextEvent;
//import org.springframework.stereotype.Component;
//
//@Component
//@Log4j2
//public class InitTestData implements ApplicationListener<ApplicationContextEvent> {
//
//    @Autowired
//    private PostRepository postRepository;
//
//    @Autowired
//    private AppUserRepository appUserRepository;
//
//    @Override
//    public void onApplicationEvent(ApplicationContextEvent applicationContextEvent) {
//        postRepository.save(new Post("Post title nr1","Post content nr2"));
//        postRepository.save(new Post("Post title nr3","Post content nr4"));
//        postRepository.save(new Post("Post title nr5","Post content nr6"));
//        System.out.println("done");
//
//        appUserRepository.save(new AppUser("wj@prajumsook.com","passw0rd","USER"));
//    }
//
//
//}
