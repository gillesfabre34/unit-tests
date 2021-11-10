package com.airbus.retex.configuration.serializer;

import com.airbus.retex.business.dto.TranslateDto;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.service.translate.ITranslateService;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.IOException;
import java.util.Locale;

@JsonComponent
public class TranslateDtoSerializer extends StdSerializer<TranslateDto> {

    @Autowired
    private ITranslateService translateService;

    public TranslateDtoSerializer() {
        super(TranslateDto.class);
    }

    @Override
    public void serialize(TranslateDto translateDto, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        Locale locale = LocaleContextHolder.getLocale();
        jgen.writeString(translateService.manageDefaultValue(translateDto, Language.languageFor(locale)));
    }
}
