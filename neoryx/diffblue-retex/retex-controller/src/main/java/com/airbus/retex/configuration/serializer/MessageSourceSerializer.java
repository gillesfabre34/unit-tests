package com.airbus.retex.configuration.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.IOException;
import java.util.Locale;

@JsonComponent
public class MessageSourceSerializer extends StdSerializer<MessageSourceResolvable> {

    @Autowired
    private MessageSource messageSource;

    protected MessageSourceSerializer() {
        super(MessageSourceResolvable.class);
    }

    @Override
    public void serialize(MessageSourceResolvable value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        Locale locale = LocaleContextHolder.getLocale();

        gen.writeString(messageSource.getMessage(value, locale));
    }
}
