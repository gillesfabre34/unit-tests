package com.airbus.retex.configuration.serializer;

import com.airbus.retex.model.common.Language;

import java.util.Map;

public interface ITranslatedFields<E extends Enum> extends Map<Language, Map<E, String>> {

}
