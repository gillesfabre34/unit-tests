package com.airbus.retex.configuration;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FeatureRightVoter implements AccessDecisionVoter<Object> {

    public static final String FEATURE_PREFIX = "FEATURE_";
    public static final String FEATURE_RIGHT_SEPARATOR = ":";

    public static final String RIGHT_READ = "READ";
    public static final String RIGHT_WRITE = "WRITE";

    public static final Pattern FEATURE_PATTERN = Pattern.compile(FEATURE_PREFIX+"([A-Z_]+)"+FEATURE_RIGHT_SEPARATOR+"?("+RIGHT_READ+"|"+RIGHT_WRITE+")?");

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return (attribute.getAttribute() != null) && (FEATURE_PATTERN.matcher(attribute.getAttribute()).find());
    }

    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        if (authentication == null) {
            return ACCESS_DENIED;
        }
        int result = ACCESS_ABSTAIN;
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (ConfigAttribute attribute : attributes) {
            if (this.supports(attribute)) {
                result = ACCESS_DENIED;

                //Extract feature code and right level from the feature-right string
                Matcher matcher = FEATURE_PATTERN.matcher(attribute.getAttribute());
                if(matcher.find()) {
                    String requiredFeatureCodeStr = matcher.group(1);
                    String requiredRightStr = matcher.group(2);

                    // Attempt to find a matching granted authority
                    for (GrantedAuthority authority : authorities) {
                        String authorityStr = authority.getAuthority();

                        //Check if right level is present in the required feature-right
                        if(requiredRightStr != null) {
                            //Check the authority concerns the same feature of the required feature-right
                            if(authorityStr.indexOf(requiredFeatureCodeStr) == FEATURE_PREFIX.length()) {
                                String authorityRightStr = authorityStr.substring(FEATURE_PREFIX.length()+requiredFeatureCodeStr.length()+1);
                                //Check the right level (NB: WRITE level allow READ level too)
                                if(requiredRightStr.equals(authorityRightStr)
                                        || (RIGHT_READ.equals(requiredRightStr) && RIGHT_WRITE.equals(authorityRightStr))) {
                                    return ACCESS_GRANTED;
                                }
                            }
                        } else {
                            //No right level, so we check only the feature
                            if(authorityStr.startsWith(requiredFeatureCodeStr)) {
                                return ACCESS_GRANTED;
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    @Override
    public boolean supports(Class clazz) {
        return true;
    }
}
