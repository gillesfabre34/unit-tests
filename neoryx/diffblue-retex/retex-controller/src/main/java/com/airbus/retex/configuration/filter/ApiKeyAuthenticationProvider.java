package com.airbus.retex.configuration.filter;

import com.airbus.retex.config.RetexConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private RetexConfig  retexConfig;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        ApiKeyAuthentication auth = (ApiKeyAuthentication) authentication;
        if(List.of(retexConfig.getThingworxApikey()).contains(auth.getApiKey())){
            auth.setAuthenticated(true);
            return auth;
        }else {
            throw new AuthenticationServiceException("Token does not contain a valid key");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiKeyAuthentication.class.isAssignableFrom(authentication);
    }
}
