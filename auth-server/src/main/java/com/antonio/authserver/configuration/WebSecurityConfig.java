package com.antonio.authserver.configuration;

import com.antonio.authserver.configuration.auth_providers.UsernameAndPasswordAuthProvider;
import com.antonio.authserver.configuration.filters.JwtTokenVerifier;
import com.antonio.authserver.model.exceptions.RestAccessDeniedHandler;
import com.antonio.authserver.model.exceptions.RestAuthenticationEntryPoint;
import com.antonio.authserver.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableSwagger2
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(2)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private RestAccessDeniedHandler accessDeniedHandler;
    private RestAuthenticationEntryPoint unauthorizedHandler;
    private static final List<String> DEFAULT_PERMIT_ALL = Collections.unmodifiableList(Collections.singletonList("*"));

    private final UsernameAndPasswordAuthProvider usernameAndPasswordAuthProvider;
    private final JwtService jwtService;
    private final UserService userService;
    private final PrivilegeService privilegeService;
    private final RoleService roleService;
    private final CustomTokenBasedRememberMeService tokenBasedRememberMeService;
    private RememberMeAuthenticationProvider rememberMeAuthenticationProvider;


    @Autowired
    public WebSecurityConfig(UsernameAndPasswordAuthProvider usernameAndPasswordAuthProvider, JwtService jwtService, UserService userService, PrivilegeService privilegeService, RoleService roleService, CustomTokenBasedRememberMeService tokenBasedRememberMeService, RememberMeAuthenticationProvider rememberMeAuthenticationProvider) {
        this.usernameAndPasswordAuthProvider = usernameAndPasswordAuthProvider;
        this.jwtService = jwtService;
        this.userService = userService;
        this.privilegeService = privilegeService;
        this.roleService = roleService;
        this.tokenBasedRememberMeService = tokenBasedRememberMeService;
        this.rememberMeAuthenticationProvider = rememberMeAuthenticationProvider;
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        // @formatter:off
        http.cors().configurationSource(corsConfigurationSource())
                .and().csrf().disable().
                authorizeRequests()
                .antMatchers("/oauth/**", "/admin/**", "/api/**").permitAll()
                .antMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler).authenticationEntryPoint(unauthorizedHandler)
                .and()
                .addFilterAfter(new JwtTokenVerifier(jwtService, userService, privilegeService, roleService), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .rememberMe().rememberMeServices(tokenBasedRememberMeService);
        /// @formatter:on
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(DEFAULT_PERMIT_ALL);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Origin", "Resource", "Request_Type", "Content-Type", "Accept", "whitelist", "X-Requested-With", "Origin", "Authorization", "Accept-Encoding", "X-Auth-Token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(usernameAndPasswordAuthProvider);
        auth.authenticationProvider(rememberMeAuthenticationProvider);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


}
