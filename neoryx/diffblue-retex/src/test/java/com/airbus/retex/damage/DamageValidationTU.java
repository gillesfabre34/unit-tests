package com.airbus.retex.damage;

import com.airbus.retex.business.util.ConstantRegex;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class DamageValidationTU {

    @Test
    public void validateNameOk() {
        assertThat(Pattern.matches(ConstantRegex.REGEX_ALPHA_ACCENT_DASH_SPACE, "Corrosion"), equalTo(true));
    }

    @Test
    public void validateNameOkAccent(){
        assertThat(Pattern.matches(ConstantRegex.REGEX_ALPHA_ACCENT_DASH_SPACE, "à - â - ä - é - è - ê - ë - ï - î - ô - ö - ù - û - ü - ÿ - ç"), equalTo(true));
    }

    @Test
    public void validateNameSpaceOk() {
        assertThat(Pattern.matches(ConstantRegex.REGEX_ALPHA_ACCENT_DASH_SPACE, "Corrosion And Pitting"), equalTo(true));
    }

    @Test
    public void validateNameSpecialCharKo() {
        assertThat(Pattern.matches(ConstantRegex.REGEX_ALPHA_ACCENT_DASH_SPACE, "@"), equalTo(false));
    }

    @Test
    public void validateNameUnderScoreKo() {
        assertThat(Pattern.matches(ConstantRegex.REGEX_ALPHA_ACCENT_DASH_SPACE, "_"), equalTo(false));
    }

    @Test
    public void validateNameNumberKo() {
        assertThat(Pattern.matches(ConstantRegex.REGEX_ALPHA_ACCENT_DASH_SPACE, "02"), equalTo(false));
    }

}
