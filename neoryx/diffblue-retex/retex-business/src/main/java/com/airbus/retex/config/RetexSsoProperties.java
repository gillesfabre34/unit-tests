package com.airbus.retex.config;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class RetexSsoProperties {

    @Getter
    @Setter
    private boolean automaticRedirect;

    @Getter
    @Setter
    private String successRedirectUri;

    @Getter
    //TODO: remove 'roleIdMapping' when roles will be safely retrievable
    private final Map<String, Long> roleIdMapping = new HashMap<>();

    @Getter
    private final Map<String, RetexSsoClientProperties> client = new HashMap<>();
}
