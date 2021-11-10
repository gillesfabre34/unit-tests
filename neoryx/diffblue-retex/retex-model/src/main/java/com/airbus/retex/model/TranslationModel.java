package com.airbus.retex.model;

import com.airbus.retex.model.common.Language;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TranslationModel<E extends Enum> {
    private Language language;
    private E field;
    private String value;
}
