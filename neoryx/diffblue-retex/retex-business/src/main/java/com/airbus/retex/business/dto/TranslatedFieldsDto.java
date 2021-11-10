package com.airbus.retex.business.dto;

import com.airbus.retex.model.common.Language;

import java.util.HashMap;
import java.util.Map;

public class TranslatedFieldsDto<E extends Enum> extends HashMap<Language, Map<E, String>> {
}
