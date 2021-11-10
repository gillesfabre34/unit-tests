package com.airbus.retex;

import com.airbus.retex.model.common.Language;

import java.util.Map;

public interface FullyTranslatedDto<E extends Enum> {

    void setTranslatedFields(Map<Language, Map<E, String>> translatedFields);

    Map<Language, Map<E, String>> getTranslatedFields();
}
