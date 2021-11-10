package com.airbus.retex.business.messages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class CommonMessages {

    @Autowired
    private MessageSource messageSource;

    /**
     *
     * @param msgCode
     * @return
     * @deprecated Should not be used anymore
     */
    @Deprecated(forRemoval = true)
    public String toLocale(String msgCode) {
        Locale locale = LocaleContextHolder.getLocale();

        return messageSource.getMessage(msgCode, null, locale);
    }
}
