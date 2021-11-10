package com.airbus.retex.model;

import com.airbus.retex.model.common.Language;

import java.util.Set;

public interface TranslatableModel<T extends AbstractTranslation<E>, E extends Enum> {

    Set<T> getTranslations();

    void addTranslation(T translation);

    /**
     * @param lang
     * @param field
     * @return value or null if there is no value
     */
    default String getTranslation(Language lang, E field) {
        String value = null;
        for (T i : getTranslations()) {
            if (field.equals(i.getField()) && lang.equals(i.getLanguage())) {
                value = i.value;
            }
        }
        return value;
    }

}
