package com.airbus.retex.configuration;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

@Component
public class CustomLocalResolver extends AcceptHeaderLocaleResolver implements WebMvcConfigurer {

    //FIXME maybe this should be at controller level and in a @Configuration class (no need of subclassing AcceptHeaderLocaleResolver) ?
    public CustomLocalResolver() {
        this.setDefaultLocale(Locale.ENGLISH);
        this.setSupportedLocales(List.of(Locale.ENGLISH, Locale.FRENCH));
    }

}