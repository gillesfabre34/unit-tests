package com.airbus.retex.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.airbus.retex.business.mapper.MapperUtils;
import com.airbus.retex.config.RetexConfig;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.admin.role.Role;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.user.User;
import com.airbus.retex.persistence.admin.RoleRepository;
import com.airbus.retex.persistence.admin.UserRepository;
import com.airbus.retex.service.admin.IUserService;
import com.airbus.retex.service.translate.ITranslateService;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserAuthDetailService {

    public static final String NO_AUTHORITY = "NO_AUTHORITY";

    @Autowired
    private RetexConfig retexConfig;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IUserService userService;

    @Autowired
    private ITranslateService translateService;

    private static final String USER_FIELD_STAFF_NUMBER = "staffNumber";
    private static final String USER_FIELD_FIRSTNAME = "firstName";
    private static final String USER_FIELD_LASTNAME = "lastName";
    private static final String USER_FIELD_EMAIL = "email";
    private static final Set<String> madatoryUserCreationFields;
    static {
        Set<String> tmp = new HashSet<>();
        tmp.add(USER_FIELD_STAFF_NUMBER);
        tmp.add(USER_FIELD_FIRSTNAME);
        tmp.add(USER_FIELD_LASTNAME);
        madatoryUserCreationFields = Collections.unmodifiableSet(tmp);
    }


    public UserAuthDetail loadAndUpdateWithEmail(Set<String> roleNames, Map<String, String> profile) throws UserAuthDetailException {
        String userEmail = profile.get(UserAuthDetailService.USER_FIELD_EMAIL);
        return loadAndUpdateWithEmail(userEmail, roleNames, profile);
    }

    public UserAuthDetail loadAndUpdateWithEmail(String email, Set<String> roleNames, Map<String, String> profile) throws UserAuthDetailException {
        if (null == roleNames) {
            throw new IllegalArgumentException("roleNames");
        }

        if (null == profile) {
            throw new IllegalArgumentException("profile");
        }

        log.debug("loadAndUpdateWithEmail: " +  email);
        if (email == null || email.isEmpty()) {
            throw new UserAuthDetailException("No email");
        }

        Set<Role> roles = getRoles(roleNames);
        if (log.isDebugEnabled()) {
            log.debug("Roles: {}", roles.stream()
                    .map(r -> translateService.getFieldValue("Role", r.getId(), Role.FIELD_LABEL, Language.EN))
                    .collect(Collectors.toSet()));
        }

        User u;
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isPresent()) {
            u = user.get();
            u = updateUser(u, roles, profile);
        } else {
            Set<String> missingFields = madatoryUserCreationFields.stream()
                    .filter(f -> !profile.containsKey(f))
                    .collect(Collectors.toSet());
            if (!missingFields.isEmpty()) {
                throw new UserAuthDetailException("Missing fields: " + missingFields);
            }
            u = new User();
            u.setEmail(email);
            u.setLanguage(Language.FR);
            u.setAirbusEntityId(retexConfig.getDefaultEntityId());
            u = updateUser(u, roles, profile);
        }
        return load(u);
    }

    public UserAuthDetail loadWithEmail(String email) throws UserAuthDetailException {
        log.debug("loadWithEmail: " + email);
        if (email == null || email.isEmpty()) {
            throw new UserAuthDetailException("No email");
        }
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isPresent()) {
            return load(user.get());
        }
        throw new UserAuthDetailException("User not found");
    }

    private UserAuthDetail load(User user) {
        Map<FeatureCode, EnumRightLevel> featureRights = userService.getUserFeatureRights(user);
        List<GrantedAuthority> authorities = new ArrayList<>();

        featureRights.forEach(((featureCode, enumRightLevel) -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_"+featureCode.name() + ":" + enumRightLevel));//FIXME factorize constants "FEATURE_" with FeatureRightVoter
            if(EnumRightLevel.WRITE.equals(enumRightLevel)) {
                //FIXME Implements a custom voter
                authorities.add(new SimpleGrantedAuthority("ROLE_"+featureCode.name() + ":" + EnumRightLevel.READ));
            }
        }));

        if(authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority(NO_AUTHORITY));
        }

        return new UserAuthDetail(user, featureRights, authorities);
    }

    private User updateUser(User user, Set<Role> roles, Map<String, String> profile) throws UserAuthDetailException {
        MapperUtils.updateList(roles, user.getRoles(),
                MapperUtils.makeIsSameLambda(Role::getId),
                Function.identity());
        profile.forEach((k,v) -> {
            if (USER_FIELD_STAFF_NUMBER.equals(k)) user.setStaffNumber(v);
            else if (USER_FIELD_FIRSTNAME.equals(k)) user.setFirstName(v);
            else if (USER_FIELD_LASTNAME.equals(k)) user.setLastName(v);
        });

        userRepository.saveAndFlush(user);
        Optional<User> u = userRepository.findByEmail(user.getEmail());
        return u.orElseThrow(()-> new UserAuthDetailException("Internal error")); //The exception should not happen
    }

    private Set<Role> getRoles(Set<String> roles) {
        Map<String, Long> ssoRoleMapping = retexConfig.getSso().getRoleIdMapping();
        return roles.stream()
                .map(ssoRoleMapping::get)
                .filter(Objects::nonNull)
                .map(r -> roleRepository.getOne(r))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }


    public static class UserAuthDetail {

        @Getter
        private User user;

        @Getter
        private Map<FeatureCode, EnumRightLevel> featureRights;

        @Getter
        private List<GrantedAuthority> authorities;

        UserAuthDetail(User user, Map<FeatureCode, EnumRightLevel> featureRights, List<GrantedAuthority> authorities) {
            this.user = user;
            this.featureRights = featureRights;
            this.authorities = authorities;
        }
    }

    public static class UserAuthDetailException extends Exception
    {
        public UserAuthDetailException(String message) {
            super(message);
        }
    }
}
