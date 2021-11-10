package com.airbus.retex.configuration.serializer;

import com.airbus.retex.business.dto.TranslationDto;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.service.translate.ITranslationService;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.IOException;
import java.util.Locale;

@JsonComponent
public class TranslationDtoSerializer extends StdSerializer<TranslationDto> {

    @Autowired
    private ITranslationService translationService;

    public TranslationDtoSerializer() {
        super(TranslationDto.class);
    }

    @Override
    public void serialize(TranslationDto translateDto, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        Locale locale = LocaleContextHolder.getLocale();
        jgen.writeString(translationService.manageTranslationDefaultValue(translateDto, Language.languageFor(locale)));
    }
}
