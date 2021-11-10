package com.airbus.retex.configuration.serializer;

import com.airbus.retex.business.dto.TranslateFieldsDto;
import com.airbus.retex.service.translate.ITranslateService;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class TranslateFieldsDtoSerializer extends StdSerializer<TranslateFieldsDto> {

    @Autowired
    private ITranslateService translateService;

    public TranslateFieldsDtoSerializer() {
        super(TranslateFieldsDto.class);
    }

    @Override
    public void serialize(TranslateFieldsDto translateFieldsDto, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeObject(translateService.getTranslatedFields(translateFieldsDto.getClassName(),
                translateFieldsDto.getEntityId(), translateFieldsDto.getFields()));
    }
}
