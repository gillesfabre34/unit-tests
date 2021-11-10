package com.airbus.retex.config;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Validated
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = RetexConfig.PREFIX)
public class RetexConfig {

    public static final String PREFIX = "retex";

    @Valid
    @NotEmpty
    private String scheme;

    @Valid
    @NotEmpty
    private String host;

    @Valid
    private boolean swaggerEnabled;

    @Valid
    private Long defaultEntityId;

    @Valid
    private String mediaDirectory;

    @Valid
    private String importsFolder;

    @Valid
    private char importsDelimiter;

    @Valid
    private char importsQuote;

    @Valid
    private int importsBatchSize;

    @Valid
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration sessionTimeout = Duration.ofMinutes(30);

    @Valid
    @NotEmpty
    private String ehcachePath;

    private final RetexSsoProperties sso = new RetexSsoProperties();

    /** -----------------------------------
     *  ----  accepted media formats ------
     *  -----------------------------------*/
    @Valid
    @NotEmpty
    private String[] mediaFormats;
    @Valid
    @NotEmpty
    private String[] partAcceptedMediaFormats;
    @Valid
    @NotEmpty
    private String[] damageAcceptedMediaFormats;
    @Valid
    @NotEmpty
    private String[] routingComponentAcceptedMediaFormats;
    @Valid
    @NotEmpty
    private String[] stepAcceptedMediaFormats;
    @Valid
    @NotEmpty
    private String[] requestAcceptedMediaFormats;

    /** -----------------------------------
     *  ----  accepted media formats ------
     *  -----------------------------------*/

    @Valid
    @URL
    private String thingworxUrl;
    @Valid
    private String thingworxApikey;

}
