package com.antonio.authserver.configuration;

import com.antonio.authserver.configuration.auth_providers.UsernameAndPasswordAuthProvider;
import com.antonio.authserver.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static com.antonio.authserver.utils.SecurityConstants.TOKEN_SECRET;

@Configuration
@EnableWebSecurity
@Order(1)
public class ApiSecurityConfig extends WebSecurityConfigurerAdapter {


    private String tokenKey = TOKEN_SECRET;
    private final UsernameAndPasswordAuthProvider usernameAndPasswordAuthProvider;
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    public ApiSecurityConfig(UsernameAndPasswordAuthProvider usernameAndPasswordAuthProvider, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.usernameAndPasswordAuthProvider = usernameAndPasswordAuthProvider;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @Override protected void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/api/**")
                .csrf()
                .disable()
                .antMatcher("/admin/**").csrf().disable()
                .antMatcher("/oauth/**").csrf().disable()
                .addFilterBefore(rememberMeAuthenticationFilter(), BasicAuthenticationFilter.class )
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .exceptionHandling().authenticationEntryPoint(new Http403ForbiddenEntryPoint());
    }

    /**
     * Remember me config
     */

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(rememberMeAuthenticationProvider());
    }
    @Bean
    public RememberMeAuthenticationFilter rememberMeAuthenticationFilter() throws Exception{
        return new RememberMeAuthenticationFilter(authenticationManager(), tokenBasedRememberMeService());
    }
    @Bean public CustomTokenBasedRememberMeService tokenBasedRememberMeService(){
        CustomTokenBasedRememberMeService service = new CustomTokenBasedRememberMeService(tokenKey, usernameAndPasswordAuthProvider);
        service.setAlwaysRemember(true);
        service.setCookieName("at");
        return service;
    }
    @Bean
    RememberMeAuthenticationProvider rememberMeAuthenticationProvider(){
        return new RememberMeAuthenticationProvider(tokenKey);
    }
}
