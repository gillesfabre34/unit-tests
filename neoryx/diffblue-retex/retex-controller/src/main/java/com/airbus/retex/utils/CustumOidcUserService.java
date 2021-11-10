package com.airbus.retex.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import com.airbus.retex.model.admin.role.Role;
import com.airbus.retex.model.admin.role.RoleCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.airbus.retex.business.authentication.CustomUserDetails;
import com.airbus.retex.config.RetexSsoClientProperties;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.user.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustumOidcUserService {

    @Autowired
    private UserAuthDetailService userAuthDetailService;

    public OidcUser create(RetexSsoClientProperties clientProperties, OidcUser oidcUser) {
        if (null == clientProperties) {
            throw new IllegalArgumentException("clientProperties");
        }

        if (log.isDebugEnabled()) {
            log.debug("OidcUser: {}", oidcUser);
        }

        Set<String> roles = getRoles(clientProperties, oidcUser);
        if (log.isDebugEnabled()) {
            log.debug("RoleNames: {}", roles);
        }

        Map<String, String> profile = getProfile(clientProperties, oidcUser);
        if (log.isDebugEnabled()) {
            log.debug("Profile: {}", profile);
        }

        UserAuthDetailService.UserAuthDetail userAuthDetail;
        try {
            userAuthDetail = userAuthDetailService.loadAndUpdateWithEmail(roles, profile);
        }
        catch (UserAuthDetailService.UserAuthDetailException e) {
            OAuth2Error oauth2Error = new OAuth2Error("Invalid retex user: " + e.getMessage());
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }

        if (log.isDebugEnabled()) {
            log.debug("Granted authorities: {}", userAuthDetail.getAuthorities());
        }

        return new UserInfo(oidcUser, userAuthDetail.getAuthorities(), userAuthDetail.getUser(), userAuthDetail.getFeatureRights());
    }

    private Map<String, String> getProfile(RetexSsoClientProperties clientProperties, OidcUser oidcUser) {
        Map<String, String> profile = new HashMap<>();
        Map<String, Object> claims = oidcUser.getClaims();
        Map<String, String> profileMapping = clientProperties.getReversedProfileMapping();

        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            String key = profileMapping.get(entry.getKey());
            if (key != null && entry.getValue() instanceof String) {
                String value = (String) entry.getValue();
                if (value != null && !value.isEmpty()) {
                    profile.put(key, value);
                }
            }
        }
        return profile;
    }

    private Set<String> getRoles(RetexSsoClientProperties clientProperties, OidcUser oidcUser) {
        Map<String, String> roleMapping = clientProperties.getReversedRoleMapping();
        List<String> inputRoles = oidcUser.getClaimAsStringList(clientProperties.getRoleListClaim());

        if (inputRoles == null) {
            return Collections.emptySet();
        }

        return inputRoles.stream()
                .map(r -> {
                    Matcher m = clientProperties.getRoleCapture().matcher(r);
                    return m.find() ? m.group(1) : null;
                })
                .filter(Objects::nonNull)
                .map(roleMapping::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    static class UserInfo extends DefaultOidcUser implements CustomUserDetails {
        private User user;
        private Map<FeatureCode, EnumRightLevel> featureRights;

        public UserInfo(OidcUser oidcUser, List<GrantedAuthority> authorities, User user, Map<FeatureCode, EnumRightLevel> featureRights) {
            super(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
            this.user = user;
            this.featureRights = featureRights;
        }

        @Override
        public User getUser() {
            return user;
        }

        @Override
        public Map<FeatureCode, EnumRightLevel> getFeatureRights() {
            return featureRights;
        }

        @Override
        public Language getLanguage() {
            return user.getLanguage();
        }

        @Override
        public Long getAirbusEntityId() {
            return user.getAirbusEntity().getId();
        }

        @Override
        public String getAirbusEntityName() {
            return user.getAirbusEntity().getCountryName();
        }

        @Override
        public Long getUserId() {
            return user.getId();
        }

        @Override
        public boolean isRole(List<RoleCode> roleCodeList) {
            return user.getRoles().stream().map(Role::getRoleCode).anyMatch(roleCodeList::contains);
        }
    }
}
