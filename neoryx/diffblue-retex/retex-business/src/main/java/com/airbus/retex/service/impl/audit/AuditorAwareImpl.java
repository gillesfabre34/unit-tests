package com.airbus.retex.service.impl.audit;

import com.airbus.retex.business.authentication.CustomUserDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


import java.util.Optional;

/**
 * Implements AuditorAware in order to inject the current user into the CRUD modifier user
 */
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            return Optional.of(userDetails.getUserId().toString());
        }

        return Optional.empty();
    }
}
