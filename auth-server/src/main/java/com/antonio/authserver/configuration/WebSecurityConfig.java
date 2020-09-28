package com.antonio.authserver.configuration;

import com.antonio.authserver.configuration.auth_providers.UsernameAndPasswordAuthProvider;
import com.antonio.authserver.configuration.filters.JwtTokenVerifier;
import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.model.ResponseMessage;
import com.antonio.authserver.model.exceptions.RestAccessDeniedHandler;
import com.antonio.authserver.model.exceptions.RestAuthenticationEntryPoint;
import com.antonio.authserver.service.JwtService;
import com.antonio.authserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private RestAccessDeniedHandler accessDeniedHandler;
    private RestAuthenticationEntryPoint unauthorizedHandler;
    @Autowired
    private UsernameAndPasswordAuthProvider usernameAndPasswordAuthProvider;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        // @formatter:off
        http.csrf().disable().
                authorizeRequests()
                .antMatchers("/oauth/**", "/admin/**").permitAll()
                .antMatchers("/api/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler).authenticationEntryPoint(unauthorizedHandler)
                .and()
                .addFilterAfter(new JwtTokenVerifier(jwtService, userService), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // @formatter:on
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(usernameAndPasswordAuthProvider);
    }


    @PostMapping
    public ResponseEntity<?> saveUser(@RequestBody final AppUserDto user) {
        userService.create(user);
        final ResponseMessage responseMessage = new ResponseMessage("User successfully saved");
        return ResponseEntity.ok().body(responseMessage);
    }


    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


}
