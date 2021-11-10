package com.airbus.retex.configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public interface ConfigurationOverride {
    void configureBefore(HttpSecurity http) throws Exception;
    boolean ssoAutomaticRedirect(boolean ssoAutomaticRedirect);
}
