package com.antonio.authserver.configuration;

import com.antonio.authserver.configuration.auth_providers.UsernameAndPasswordAuthProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.antonio.authserver.utils.SecurityConstants.HEADER_AUTHORIZATION;

public class CustomTokenBasedRememberMeService extends TokenBasedRememberMeServices {

    public CustomTokenBasedRememberMeService(String key, UsernameAndPasswordAuthProvider userDetailsService) {
        super(key, userDetailsService);
    }

    private final String HEADER_SECURITY_TOKEN = HEADER_AUTHORIZATION;

    /**
     * Locates the Spring Security remember me token in the request and returns its value.
     *
     * @param request the submitted request which is to be authenticated
     * @return the value of the request header (which was originally provided by the cookie - API expects it in header)
     */
    @Override
    protected String extractRememberMeCookie(HttpServletRequest request) {
        String token = request.getHeader(HEADER_SECURITY_TOKEN);
        if ((token == null) || (token.length() == 0)) {
            return null;
        }

        return token;
    }


}
