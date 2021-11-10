package com.airbus.retex;

import com.airbus.retex.business.authentication.CustomUserDetails;
import com.airbus.retex.model.admin.role.Role;
import com.airbus.retex.model.admin.role.RoleCode;
import com.airbus.retex.utils.UserAuthDetailService;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import java.util.*;


@Slf4j
public class FakeAuthenticationProvider implements AuthenticationProvider {

    private UserAuthDetailService userAuthDetailService;

    public FakeAuthenticationProvider(UserAuthDetailService userAuthDetailService) {
        this.userAuthDetailService = userAuthDetailService;
    }

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        FakeUserAuth auth = (FakeUserAuth) authentication;

        UserAuthDetailService.UserAuthDetail userAuthDetail;
        try {
            if (auth.roles != null) {
                userAuthDetail = userAuthDetailService.loadAndUpdateWithEmail(auth.name, auth.roles, auth.profile);
            } else {
                userAuthDetail = userAuthDetailService.loadWithEmail(auth.name);
            }
        }
        catch (UserAuthDetailService.UserAuthDetailException e) {
            log.debug("Invalid retex user: " + e.getMessage());
            throw new FakeAuthenticationException(e.getMessage());
        }

        auth.authenticate(userAuthDetail.getUser(), userAuthDetail.getFeatureRights(), userAuthDetail.getAuthorities());
        return auth;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication == FakeUserAuth.class;
    }

    public static class FakeUserAuth implements Authentication {
        private String name;
        private boolean isAuthenticated = false;
        private Set<String> roles = null;
        private Map<String, String> profile = null;

        private FakeUserInfo userInfo = null;
        private List<GrantedAuthority> authorities = null;

        public FakeUserAuth(String name) {
            this.name = name;
        }

        public void setRoles(Collection<String> roles) {
            this.roles = new HashSet<String>(roles);
            initProfile();
        }

        public void setProfile(Collection<String> profileEntries) {
            initProfile();
            profileEntries.forEach(e -> {
                String[] d = e.split(":");
                if (d.length == 2) {
                    profile.put(d[0], d[1]);
                } else {
                    log.error("Invalid fakeProfileEntry: " + e);
                }
            });
        }

        private void initProfile() {
            if (profile == null) {
                profile = new HashMap<String, String>();
            }
        }

        public void authenticate(User user, Map<FeatureCode, EnumRightLevel> featureRights, List<GrantedAuthority> authorities) {
            this.userInfo = new FakeUserInfo(user, featureRights);
            this.authorities = authorities;
            this.isAuthenticated = true;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getDetails() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return userInfo;
        }

        @Override
        public boolean isAuthenticated() {
            return this.isAuthenticated;
        }

        @Override
        public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
            if (isAuthenticated) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public String getName() {
            return name;
        }
    }

    private static class FakeUserInfo implements CustomUserDetails {

        private User user = null;
        private Map<FeatureCode, EnumRightLevel> featureRights = null;

        public FakeUserInfo(User user, Map<FeatureCode, EnumRightLevel> featureRights) {
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

    public static class FakeAuthenticationException extends AuthenticationException {
        FakeAuthenticationException(String msg) {
            super("FakeAuthenticationException: " + msg);
        }
    }
}
