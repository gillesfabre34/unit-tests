package com.airbus.retex;

import com.airbus.retex.configuration.ConfigurationOverride;
import com.airbus.retex.utils.UserAuthDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;


@Configuration
public class FakeAuthenticationConfigurationOverride implements ConfigurationOverride {

    @Autowired
    private UserAuthDetailService userAuthDetailService;

    private boolean initDone = false;
    private FakeAuthenticationProvider fakeAuthenticationProvider;
    private FakeAuthenticationFilter fakeAuthenticationFilter;

    private void init() {
        if (!initDone) {
            fakeAuthenticationProvider = new FakeAuthenticationProvider(userAuthDetailService);
            fakeAuthenticationFilter = new FakeAuthenticationFilter();
            initDone = true;
        }
    }

    @Override
    public void configureBefore(HttpSecurity http) throws Exception {
        init();
        http
            .authenticationProvider(fakeAuthenticationProvider)
            .addFilterBefore(fakeAuthenticationFilter, BasicAuthenticationFilter.class)
            ;
    }

    @Override
    public boolean ssoAutomaticRedirect(boolean ssoAutomaticRedirect) {
        return false; // Force no redirection
    }
}
