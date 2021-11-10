package com.airbus.retex.model.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.util.Locale;

@Getter
@ApiModel
public enum Language {

    @ApiModelProperty(value = "For French  language")
    FR(Locale.FRENCH),
    @ApiModelProperty(value = "For English language")
    EN(Locale.ENGLISH);

    private static final Language DEFAULT_LANGUAGE = Language.EN;

    public final Locale locale;

    Language(Locale locale) {
        this.locale = locale;
    }

    public static Language getDefault() {
        return DEFAULT_LANGUAGE;
    }

    public boolean isDefault() {
        return EN.equals(this);
    }

    public static Language languageFor(Locale locale) {
        Language result;
        if (locale.toString().contains("_")) {
            String lang = locale.toString().split("_")[1];
            if (lang.equals("US")) {
                lang = "EN";
            }
            result = Language.valueOf(lang);
        } else {
            result = Language.valueOf(locale.toString().toUpperCase());
        }
        for (var language : Language.values()) {
            if (language.equals(result)) {
                return language;
            }
        }
        return Language.getDefault();
    }


}

