package com.antonio.authserver.configuration;

import com.antonio.authserver.configuration.auth_providers.UsernameAndPasswordAuthProvider;
import com.antonio.authserver.configuration.filters.JwtTokenVerifier;
import com.antonio.authserver.model.exceptions.RestAccessDeniedHandler;
import com.antonio.authserver.model.exceptions.RestAuthenticationEntryPoint;
import com.antonio.authserver.service.JwtService;
import com.antonio.authserver.service.PrivilegeService;
import com.antonio.authserver.service.RoleService;
import com.antonio.authserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;


@Configuration
@EnableWebSecurity
@EnableSwagger2
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private RestAccessDeniedHandler accessDeniedHandler;
    private RestAuthenticationEntryPoint unauthorizedHandler;

    private final UsernameAndPasswordAuthProvider usernameAndPasswordAuthProvider;
    private final Environment environment;
    private final JwtService jwtService;
    private final UserService userService;
    private final PrivilegeService privilegeService;
    @Autowired
    public WebSecurityConfig(UsernameAndPasswordAuthProvider usernameAndPasswordAuthProvider, Environment environment, JwtService jwtService, UserService userService, PrivilegeService privilegeService) {
        this.usernameAndPasswordAuthProvider = usernameAndPasswordAuthProvider;
        this.environment = environment;
        this.jwtService = jwtService;
        this.userService = userService;
        this.privilegeService = privilegeService;
    }
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        // @formatter:off
        http.cors().configurationSource(corsConfigurationSource())
                .and().csrf().disable().
                authorizeRequests()
                .antMatchers("/api/user/**").hasRole("ADMIN")
                .antMatchers("/api/client/**").hasRole("ADMIN")
                .antMatchers("/api/role/**").hasRole("ADMIN")
                .antMatchers("/api/privilege/**").hasRole("ADMIN")
                .antMatchers("/api/resources/**").hasRole("ADMIN")
                .antMatchers("/oauth/**", "/admin/**", "/api/**").permitAll()
                .antMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                    .exceptionHandling().accessDeniedHandler(accessDeniedHandler).authenticationEntryPoint(unauthorizedHandler)
                .and()
                    .addFilterAfter(new JwtTokenVerifier(jwtService, userService, privilegeService), UsernamePasswordAuthenticationFilter.class)
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        /// @formatter:on
    }



    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(this.environment.getProperty("clientFrontedURL"), this.environment.getProperty("clientBackendURL"),this.environment.getProperty("authorizationServerFrontedURL")));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Origin","Resource","Request_Type", "Content-Type", "Accept", "whitelist", "X-Requested-With", "Origin", "Authorization", "Accept-Encoding", "X-Auth-Token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(usernameAndPasswordAuthProvider);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


}
