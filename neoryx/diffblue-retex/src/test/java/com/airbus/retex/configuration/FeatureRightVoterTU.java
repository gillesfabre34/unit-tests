package com.airbus.retex.configuration;

import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.common.EnumRightLevel;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;

import static com.airbus.retex.configuration.FeatureRightVoter.FEATURE_PREFIX;
import static com.airbus.retex.configuration.FeatureRightVoter.FEATURE_RIGHT_SEPARATOR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.security.access.AccessDecisionVoter.*;

public class FeatureRightVoterTU {

    private FeatureRightVoter featureRightVoter = new FeatureRightVoter();

    private Authentication asAuthentication;
    private FeatureCode withFeatureCode;
    private EnumRightLevel withRightLevel;
    private Integer expectedVote;

    @Test
    public void withFeature_withReadLevel_allowed() {
        asAuthentication = buildAuthentication(
                buildAuthority(FeatureCode.ADMIN, EnumRightLevel.READ)
        );
        withFeatureCode = FeatureCode.ADMIN;
        withRightLevel = EnumRightLevel.READ;
        expectedVote = ACCESS_GRANTED;

        check();
    }

    @Test
    public void withFeature_withWriteLevel_allowed() {
        asAuthentication = buildAuthentication(
                buildAuthority(FeatureCode.ADMIN, EnumRightLevel.WRITE)
        );
        withFeatureCode = FeatureCode.ADMIN;
        withRightLevel = EnumRightLevel.WRITE;
        expectedVote = ACCESS_GRANTED;

        check();
    }

    @Test
    public void withFeature_withWriteLevel_withOtherFeatures_allowed() {
        asAuthentication = buildAuthentication(
                buildAuthority(FeatureCode.ADMIN, EnumRightLevel.WRITE),
                buildAuthority(FeatureCode.PART_MAPPING, EnumRightLevel.WRITE),
                buildAuthority(FeatureCode.DAMAGE, EnumRightLevel.READ)
        );
        withFeatureCode = FeatureCode.ADMIN;
        withRightLevel = EnumRightLevel.WRITE;
        expectedVote = ACCESS_GRANTED;

        check();
    }

    @Test
    public void withoutFeature_withRightLevel_withOtherFeatures_notAllowed() {
        asAuthentication = buildAuthentication(
                buildAuthority(FeatureCode.PART_MAPPING, EnumRightLevel.WRITE),
                buildAuthority(FeatureCode.DAMAGE, EnumRightLevel.READ)
        );
        withFeatureCode = FeatureCode.ADMIN;
        withRightLevel = EnumRightLevel.WRITE;
        expectedVote = ACCESS_DENIED;

        check();
    }

    @Test
    public void withoutFeature_withReadLevel_withOtherFeature_allowed() {
        asAuthentication = buildAuthentication(
                buildAuthority(FeatureCode.PART_MAPPING, EnumRightLevel.WRITE),
                buildAuthority(FeatureCode.DAMAGE, EnumRightLevel.READ)
        );
        withFeatureCode = FeatureCode.DAMAGE;
        withRightLevel = EnumRightLevel.READ;
        expectedVote = ACCESS_GRANTED;

        check();
    }

    @Test
    public void withFeature_withWriteLevel_readAllowed() {
        asAuthentication = buildAuthentication(
                buildAuthority(FeatureCode.ADMIN, EnumRightLevel.READ)
        );
        withFeatureCode = FeatureCode.ADMIN;
        withRightLevel = EnumRightLevel.READ;
        expectedVote = ACCESS_GRANTED;

        check();
    }

    @Test
    public void withoutFeature_withReadLevel_notAllowed() {
        asAuthentication = buildAuthentication(
                buildAuthority(FeatureCode.PART_MAPPING, EnumRightLevel.READ)
        );
        withFeatureCode = FeatureCode.DAMAGE;
        withRightLevel = EnumRightLevel.READ;
        expectedVote = ACCESS_DENIED;

        check();
    }

    @Test
    public void withFeature_withoutWriteLevel_notAllowedWrite() {
        asAuthentication = buildAuthentication(
                buildAuthority(FeatureCode.DAMAGE, EnumRightLevel.READ)
        );
        withFeatureCode = FeatureCode.DAMAGE;
        withRightLevel = EnumRightLevel.WRITE;
        expectedVote = ACCESS_DENIED;

        check();
    }

    private Authentication buildAuthentication(String... authorities) {
        return new TestingAuthenticationToken("test", null, authorities);
    }

    private String buildAuthority(FeatureCode featureCode, EnumRightLevel enumRightLevel) {
        return FEATURE_PREFIX+featureCode.name()+FEATURE_RIGHT_SEPARATOR+enumRightLevel.name();
    }

    private void check(){
        String authority = buildAuthority(withFeatureCode, withRightLevel);
        int actual = featureRightVoter.vote(asAuthentication, new Object(), List.of(new SecurityConfig(authority)));
        assertThat(label(actual), equalTo(label(expectedVote)));
    }

    private String label(int integer) {
        if(integer == ACCESS_GRANTED) {
            return "ACCESS_GRANTED";
        } else if(integer == ACCESS_DENIED) {
            return "ACCESS_DENIED";
        } else if(integer == ACCESS_ABSTAIN) {
            return "ACCESS_ABSTAIN";
        } else {
            throw new AssertionFailedError("No label for "+integer);
        }
    }

}
