package com.airbus.retex.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;

public class RetexSsoClientProperties {

    @Getter
    @Setter
    private String roleListClaim;

    @Getter
    @Setter
    private Pattern roleCapture;

    @Getter
    // Contains the association 'role_in_the_app' => 'corresponding_roles_from_the_SSO
	private final Map<String, List<String>> roleMapping = new HashMap<>();

    // Contains the association 'role_from_the_SSO' => 'role_in_the_app'
    //   Generated from roleMapping
    private Map<String, String> reversedRoleMapping = null;
    public Map<String, String> getReversedRoleMapping() {
        if (reversedRoleMapping == null) {
			reversedRoleMapping = new HashMap<>();
            roleMapping.forEach((k, l) -> l.forEach(v -> reversedRoleMapping.put(v, k)));
        }
        return reversedRoleMapping;
    }

    @Getter
    // Contains the association 'profile_key_in_the_app' => 'corresponding_profile_key_from_the_SSO'
	private final Map<String, String> profileMapping = new HashMap<>();

    // Contains the association 'profile_key_from_the_SSO' => 'profile_key_in_the_app'
    //   Generated from profileMapping
    private Map<String, String> reversedProfileMapping = null;
    public Map<String, String> getReversedProfileMapping() {
        if (reversedProfileMapping == null) {
			reversedProfileMapping = new HashMap<>();
            profileMapping.forEach((k, v) -> reversedProfileMapping.put(v, k));
        }
        return reversedProfileMapping;
    }


}
